package sma.Model.Car;

/**
 * The state of the accelerator (Accelerating, None, Braking)
 */
public class AccelerationStates implements Cloneable {
    
    /**
     * The accelerating prototype (a positive acceleration means the car is accelerating)
     */
    public static final AccelerationStates Accelerating = new AccelerationStates(1);

    /**
     * The none prototype (no speed change)
     */
    public static final AccelerationStates None = new AccelerationStates(0);

    /**
     * The braking prototype (a negative acceleration means the car is braking)
     */
    public static final AccelerationStates Braking = new AccelerationStates(-1);

    private float factor;
    
    public AccelerationStates(float factor) {
        this.factor = factor;
    }
    
    public float getFactor() {
        return this.factor;
    }
    
    public AccelerationStates mult(float factor) {
        this.factor *= factor;
        return this;
    }

    /**
     * The comparison is based on the sign of the factor, not its value exactly
     * @param obj
     * @return 
     */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof AccelerationStates) && Math.signum(((AccelerationStates)obj).factor) == Math.signum(factor);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + Float.floatToIntBits(Math.signum(factor));
        return hash;
    }

    @Override
    public Object clone() {
        return new AccelerationStates(factor);
    }

    @Override
    public String toString() {
        return String.format("Acceleration@%.3f", factor);
    }
}
