package sma.Model.Car;

import jade.core.AID;
import java.awt.Color;
import java.io.Serializable;
import java.util.Random;
import maths.Vector2D;
import sma.Model.Map.EndPoints;

/**
 * The model encapsulates all the other properties of the car
 */
public class Model implements Serializable {
    private EndPoints startPoint;
    private AccelerationStates accelerationState;
    private Destinations destination;
    private boolean hasTurned;
    private boolean hasEngaged;
    private Vector2D position;
    private Vector2D direction;
    private AID frontCar;
    private boolean redLightAhead;
    private Color color;
    
    private static Random r = new Random();
    
    public Model() {
        this(   (EndPoints)EndPoints.random().clone(),
                (AccelerationStates)AccelerationStates.Accelerating.clone(),
                (Destinations)Destinations.random().clone(),
                false,
                false,
                null,
                true);
    }
    
    public Model(EndPoints startPoint, AccelerationStates accelerationState, Destinations destination, boolean hasTurned, boolean hasEngaged, AID frontCar, boolean redLightAhead) {
        this.startPoint = startPoint;
        this.accelerationState = accelerationState;
        this.destination = destination;
        this.hasTurned = hasTurned;
        this.hasEngaged = hasEngaged;
        this.position = (Vector2D)startPoint.getOrigin().clone();
        this.direction = Vector2D.mult(this.position, -1);
        this.frontCar = frontCar;
        this.redLightAhead = redLightAhead;
        this.color = new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255));
    }
    
    public EndPoints getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(EndPoints startPoint) {
        this.startPoint = startPoint;
    }

    public AccelerationStates getAccelerationState() {
        return accelerationState;
    }

    public void setAccelerationState(AccelerationStates accelerationState) {
        this.accelerationState = accelerationState;
    }

    public Destinations getDestination() {
        return destination;
    }

    public void setDestination(Destinations destination) {
        this.destination = destination;
    }

    public boolean isHasTurned() {
        return hasTurned;
    }

    public void setHasTurned(boolean hasTurned) {
        this.hasTurned = hasTurned;
    }

    public boolean isHasEngaged() {
        return hasEngaged;
    }

    public void setHasEngaged(boolean hasEngaged) {
        this.hasEngaged = hasEngaged;
    }

    public Vector2D getPosition() {
        return position;
    }

    public void setPosition(Vector2D position) {
        this.position = position;
    }

    public Vector2D getDirection() {
        return direction;
    }

    public void setDirection(Vector2D direction) {
        this.direction = direction;
    }
    
    public AID getFrontCar() {
        return frontCar;
    }

    public void setFrontCar(AID frontCar) {
        this.frontCar = frontCar;
    }    

    public boolean getRedLightAhead() {
        return redLightAhead;
    }

    public void setRedLightAhead(boolean redLightAhead) {
        this.redLightAhead = redLightAhead;
    }
    
    public Color getColor() {
        return this.color;
    }
}
