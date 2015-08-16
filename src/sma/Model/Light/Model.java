package sma.Model.Light;

import java.io.Serializable;
import sma.Model.Map.EndPoints;

/**
 * The model encapsulates all the other properties of the light
 * A light can be master or slave, in this case he will depend on his slave to know its state
 */
public class Model implements Serializable {
    private State state;
    private EndPoints way;
    private boolean master;
    
    public Model() {
        master = false;
        state = State.Green;
        way = EndPoints.RIGHT;
    }
    
    public Model(State state, EndPoints way, boolean master) {
        this.state = state;
        this.way = way;
        this.master = master;
    }
    
    public State getState() {
        return state;
    }
    
    public void setState(State state) {
        this.state = state;
    }
    
    public EndPoints getWay() {
        return way;
    }
    
    public void setWay(EndPoints way) {
        this.way = way;
    }
    
    public boolean isMaster() {
        return master;
    }
    
    public void setMaster(boolean master) {
        this.master = master;
    }
}
