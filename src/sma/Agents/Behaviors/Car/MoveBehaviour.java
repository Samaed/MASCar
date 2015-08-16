package sma.Agents.Behaviors.Car;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import maths.Matrix2x2;
import sma.Model.Car.AccelerationStates;
import sma.Model.Car.CarInterface;
import sma.Model.Car.Destinations;
import maths.Vector2D;
import utils.Constants;
import utils.Utils;

/**
 * Behavior used to get the car moving.
 * Calculates the acceleration and double integrate it to get the new position.
 * Calls some other behaviors when necessary
 */
public class MoveBehaviour extends TickerBehaviour {    
    private final CarInterface carInterface;
    private final float deltaTime;
    private final Destinations destination;
    
    // Those variables are computed at each onTick time
    
    private float accelerationFactor;
    private Vector2D acceleration;
    private Vector2D vectorToThreat;
    private Vector2D speed = (Vector2D)Vector2D.Zero.clone();
    private Vector2D closestThreat;
    
    // Those variables are retrieved from the carInterface at the start of onTick
    
    private Vector2D position;
    private Vector2D direction;
    private CarInterface frontCarInterface;
    
    // Those variables notify the state used to update some parts
    
    private boolean firstAsk = true;
    
    public MoveBehaviour(Agent a) {
        this(a, Constants.TICKER_DEFAULT_PERIOD);
    }
    
    public MoveBehaviour(Agent a, long period) {
        super(a, period);
        
        deltaTime = 1f/this.getPeriod();
        
        carInterface = (CarInterface)myAgent;
        destination = (Destinations)carInterface.getDestination();
    }

    @Override
    protected void onTick() {
        
        position = carInterface.getPosition();
        
        direction = carInterface.getDirection();
        accelerationFactor = carInterface.getAccelerationState().getFactor();
        frontCarInterface = Utils.getCarInterface(myAgent.getContainerController(),carInterface.getFrontCar());
        
        calculateAcceleration();
        move();
        suicideIfOutside();
        setBehaviors();
    }
    
    private void move() {
        if ((closestThreat != null && closestThreat.equals(position, .05f)))
            speed.mult(0);
        else
            speed.add(Vector2D.mult(acceleration, deltaTime));
                
        position.add(Vector2D.mult(acceleration, deltaTime*deltaTime/2))
                .add(Vector2D.mult(speed,deltaTime));
        
        carInterface.setPosition(position);
    }
    
    private void suicideIfOutside() {
        if (position.length() > 1) {
            myAgent.addBehaviour(new TellDeathManagerBehaviour());
        }
    }
    
    private void calculateAcceleration() {
        closestThreat = null;
        
        if (frontCarInterface != null)
            closestThreat = Vector2D.add(frontCarInterface.getPosition(),Vector2D.mult(frontCarInterface.getDirection(), -Constants.SAFETY_DISTANCE));

        if (carInterface.getRedLightAhead() && !carInterface.hasEngaged())
            closestThreat = (closestThreat != null && (Vector2D.add(Vector2D.mult(closestThreat, -1), position).length() < position.length())) ? closestThreat : Vector2D.Zero;

        if (closestThreat != null) {
            vectorToThreat = Vector2D.add(Vector2D.mult(closestThreat, -1), position);
            accelerationFactor = 2*(float)(Math.atan(vectorToThreat.length()) / (Math.PI / 2));
        } else {
            accelerationFactor = 1;
        }
        
        if (speed.length() > Constants.VEHICLE_MAX_SPEED) {
            accelerationFactor = 0;
        }
        
        
        acceleration = Vector2D.mult(direction, accelerationFactor);
        carInterface.setAccelerationState(new AccelerationStates(accelerationFactor));
    }
    
    private void setBehaviors() {        
        if (!carInterface.hasEngaged() && position.equals(Vector2D.Zero, Constants.DETECTION_DISTANCE) && firstAsk) {
            firstAsk = false;
            myAgent.addBehaviour(new WaitingRegistrationBehaviour(true));
        }
        
        if (position.equals(Vector2D.Zero, .05f) && !carInterface.hasTurned() && carInterface.hasEngaged()) {
            carInterface.setTurned();
            myAgent.addBehaviour(new WaitingRegistrationBehaviour(false));
            myAgent.addBehaviour(new InformTurnBehaviour());
            carInterface.setDirection(Matrix2x2.mult(destination.getMatrix(),direction));
            speed = destination.getMatrix().mult(speed);
        }
    }
}
