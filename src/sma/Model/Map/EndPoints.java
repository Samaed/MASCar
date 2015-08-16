package sma.Model.Map;

import java.io.Serializable;
import maths.Vector2D;
import utils.Randomizable;

/**
 * The map has four endpoints (Right, Left, Top and Bottom)
 */
public class EndPoints implements Cloneable, Serializable {
    
    public static final EndPoints RIGHT = new EndPoints(1f,0f);
    public static final EndPoints LEFT = new EndPoints(-1f,0f);
    public static final EndPoints BOTTOM = new EndPoints(0f,-1f);
    public static final EndPoints TOP = new EndPoints(0f,1f);
    
    private transient static final Randomizable<EndPoints> randomizer = new Randomizable<>(LEFT, RIGHT, BOTTOM, TOP);
    
    private final Vector2D origin;
    
    EndPoints(EndPoints endpoint) {
        this(endpoint.origin.getX(), endpoint.origin.getY());
    }
    
    EndPoints(float x, float y) {
        this.origin = new Vector2D(x,y);
    }
    
    public Vector2D getOrigin() {
        return this.origin;
    }
    
    public static EndPoints random() {
        return randomizer.random();
    }

    @Override
    public boolean equals(Object obj) {        
        return (obj instanceof EndPoints) && ((EndPoints)obj).origin.equals(origin);
    }

    @Override
    public int hashCode() {
        return getOrigin().hashCode();
    }
    
    @Override
    public Object clone() {
        return new EndPoints(this);
    }
}
