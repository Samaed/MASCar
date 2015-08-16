package sma.Model.Light;

import java.io.Serializable;

/**
 * A light can be turned Red, Orange or Green
 */
public enum State implements Serializable {
    Red(7,20),
    Orange(2,2),
    Green(5,18);
    
    private final float minDuration;
    private final float maxDuration;
    
    State(float minDuration, float maxDuration) {
        this.minDuration = minDuration;
        this.maxDuration = maxDuration;
    }
    
    public float getMinDuration() {
        return minDuration;
    }
    
    public float getMaxDuration() {
        return maxDuration;
    }
}
