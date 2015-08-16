package sma.Model.Car;

import java.io.Serializable;
import maths.Matrix2x2;
import utils.Randomizable;

/**
 * The destination of a car (Forward, Right, Left)
 */
public class Destinations implements Cloneable, Serializable {
    
    /**
     * The forward destination using the Identity 2x2 Matrix (the direction will stay the same)
     */
    public static final Destinations FORWARD = new Destinations((Matrix2x2)Matrix2x2.Identity.clone());

    /**
     * The right destination using the 90° clockwise 2x2 Matrix
     */
    public static final Destinations RIGHT = new Destinations((Matrix2x2)Matrix2x2.Clockwise.clone());

    /**
     * The right destination using the 90° counter-clockwise 2x2 Matrix
     */
    public static final Destinations LEFT = new Destinations((Matrix2x2)Matrix2x2.CounterClockwise.clone());
    
    private transient static final Randomizable<Destinations> randomizer = new Randomizable<>(FORWARD, RIGHT, LEFT);
    
    private Matrix2x2 matrix;

    public Destinations(Destinations destination) {
        this((Matrix2x2)destination.matrix.clone());
    }
    
    Destinations(Matrix2x2 matrix) {
        this.matrix = matrix;
    }
    
    public Matrix2x2 getMatrix() {
        return this.matrix;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Destinations) && ((Destinations)obj).matrix.equals(matrix);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.matrix.hashCode();
        return hash;
    }

    @Override
    public Object clone() {
        return new Destinations(this);
    }
    
    public static Destinations random() {
        return randomizer.random();
    }
}
