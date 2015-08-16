package maths;

import java.io.Serializable;

/**
 * A 2D Vector using floats
 */
public class Vector2D implements Cloneable, Serializable {
    
    private float x;
    private float y;

    public float getX() {
        return x;
    }
    
    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }
    
    public void setY(float y) {
        this.y = y;
    }

    /**
     * A (0,0) vector
     */
    public static final Vector2D Zero = new Vector2D();

    /**
     * The up 2D vector (0,1)
     */
    public static final Vector2D Up = new Vector2D(0f,1f);

    /**
     * The right 2D vector (1,0)
     */
    public static final Vector2D Right = new Vector2D(1f,0f);

    public Vector2D() {
        this(0,0);
    }
    
    public Vector2D(Vector2D vector) {
        this(vector.x, vector.y);        
    }
    
    public Vector2D(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Static version : creates a duplicate
     * Adds a vector to another
     * @param a
     * @param b
     * @return
     */
    public static Vector2D add(Vector2D a, Vector2D b) {
        Vector2D v = new Vector2D(a);
        return v.add(b);
    }
    
    /**
     * Adds a vector to another
     * @param other
     * @return
     */
    public Vector2D add(Vector2D other) {
        this.x += other.x;
        this.y += other.y;
        return this;
    }
    
    /**
     * Static version : creates a duplicate
     * Multiplication by a factor
     * @param a
     * @param factor
     * @return
     */
    public static Vector2D mult(Vector2D a, float factor) {
        Vector2D v = new Vector2D(a);
        return v.mult(factor);
    }
    
    /**
     * Multiplication by a factor
     * @param factor
     * @return
     */
    public Vector2D mult(float factor) {
        this.x *= factor;
        this.y *= factor;
        return this;
    }
    
    /**
     * Returns the length of the vector
     * @param a
     * @return
     */
    public static float length(Vector2D a) {
        return a.length();
    }
    
    /**
     * Returns the length of the vector
     * @return
     */
    public float length() {
        return (float)Math.sqrt((x*x)+(y*y));
    }
    
    /**
     * Static version : create a duplicate
     * Return a normalized vector
     * @param a
     * @return
     */
    public static Vector2D normalize(Vector2D a) {
        return new Vector2D(a).normalize();
    }
    
    /**
     * Return a normalized vector
     * @return
     */
    public Vector2D normalize() {
        if (this.length() == 0)
            return Vector2D.Zero;
        return this.mult(1/this.length());
    }
    
    /**
     * Equality based on a threshold
     * @param vector
     * @param threshold
     * @return
     */
    public boolean equals(Vector2D vector, float threshold) {
        return Vector2D.add(this,Vector2D.mult(vector, -1)).length() <= threshold;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vector2D)) return false;
        return equals((Vector2D)obj, 0);
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Float.floatToIntBits(this.x);
        hash = 37 * hash + Float.floatToIntBits(this.y);
        return hash;
    }

    @Override
    public Object clone() {
        return new Vector2D(this);
    }

    @Override
    public String toString() {
        return String.format("(%.3f,%.3f)", x, y);
    }
}
