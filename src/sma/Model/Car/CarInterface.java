package sma.Model.Car;

import jade.core.AID;
import java.awt.Color;
import sma.Model.Map.EndPoints;
import maths.Vector2D;

public interface CarInterface {
    public Vector2D getPosition();
    public void setPosition(Vector2D position);
    
    public Vector2D getDirection();
    public void setDirection(Vector2D direction);
    
    public EndPoints getStartPoint();
    
    public AccelerationStates getAccelerationState();
    public void setAccelerationState(AccelerationStates accelerationState);
    
    public Destinations getDestination();
    
    public boolean hasTurned();
    public void setTurned();
    
    public boolean hasEngaged();
    public void setEngaged();

    public boolean getRedLightAhead();
    public void setRedLightAhead(boolean value);

    public AID getFrontCar();
    public void setFrontCar(AID aid);
    
    public Color getColor();
}
