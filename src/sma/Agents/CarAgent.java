package sma.Agents;

import jade.core.AID;
import java.awt.Color;
import sma.Agents.Behaviors.Car.MoveBehaviour;
import sma.Model.Car.AccelerationStates;
import sma.Model.Car.CarInterface;
import sma.Model.Car.Destinations;
import sma.Model.Map.EndPoints;
import maths.Vector2D;
import sma.Agents.Behaviors.Car.CarMessagesBehaviour;
import sma.Model.Car.Model;

/**
 * A CarAgent is an AbstractAgent of type CAR implementing a CarInterface
 */
public class CarAgent extends AbstractAgent implements CarInterface {
   
    private Model model;
    
    public CarAgent() {
        super(AgentType.CAR);
    }

    @Override
    protected void takeDown() {
        super.takeDown();
    }
    
    /**
     * Initialize the car with the model passed as argument
     * or a default model otherwise
     */
    @Override
    protected void setup() {        
        super.setup();
        
        Object[] args = getArguments();
        if(args.length == 1){
            model = (Model) args[0];
        } else {
            model = new Model();
        }
        
        registerO2AInterface(CarInterface.class, this);
        
        addBehaviour(new MoveBehaviour(this));
        addBehaviour(new CarMessagesBehaviour());
    }

    @Override
    public EndPoints getStartPoint() {
        return model.getStartPoint();
    }

    @Override
    public AccelerationStates getAccelerationState() {
        return model.getAccelerationState();
    }
    
    @Override
    public void setAccelerationState(AccelerationStates accelerationState) {
        this.model.setAccelerationState(accelerationState);
    }

    @Override
    public Destinations getDestination() {
        return model.getDestination();
    }

    @Override
    public boolean hasTurned() {
        return model.isHasTurned();
    }
    
    @Override
    public void setTurned() {
        model.setHasTurned(true);
    }

    @Override
    public Vector2D getPosition() {
        return model.getPosition();
    }

    @Override
    public void setPosition(Vector2D position) {
        model.setPosition(position);
    }

    @Override
    public Vector2D getDirection() {
        return model.getDirection();
    }
    
    @Override
    public void setDirection(Vector2D direction) {
        model.setDirection(direction);
    }

    @Override
    public boolean getRedLightAhead() {
        return model.getRedLightAhead();
    }

    @Override
    public void setRedLightAhead(boolean value) {
        model.setRedLightAhead(value);
    }

    @Override
    public AID getFrontCar() {
        return model.getFrontCar();
    }

    @Override
    public void setFrontCar(AID aid) {
        model.setFrontCar(aid);
    }

    @Override
    public boolean hasEngaged() {
        return model.isHasEngaged();
    }

    @Override
    public void setEngaged() {
        model.setHasEngaged(true);
    }

    /**
     * Function used only by the GUI
     * @return
     */
    @Override
    public Color getColor() {
        return model.getColor();
    }
}
