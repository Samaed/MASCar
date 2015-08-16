package maths;

import java.io.Serializable;

/**
 * A 2x2 matrix with basic operations
 */
public class Matrix2x2 implements Serializable, Cloneable {
    
    private float[][] values;
    
    /**
     * A zero-filled matrix
     */
    public static final Matrix2x2 Zero = new Matrix2x2();

    /**
     * The 90° counter clockwise matrix
     */
    public static final Matrix2x2 CounterClockwise = new Matrix2x2(0,-1,1,0);

    /**
     * The 90° clockwise matrix
     */
    public static final Matrix2x2 Clockwise = new Matrix2x2(0,1,-1,0);

    /**
     * The identity matrix (diagonal of 1)
     */
    public static final Matrix2x2 Identity = new Matrix2x2(1,0,0,1);

    public Matrix2x2() {
        this(0,0,0,0);
    }
  
    public Matrix2x2(Matrix2x2 matrix) {
        this(matrix.get(0,0),matrix.get(0,1),matrix.get(1,0), matrix.get(1,1));
    }

    @Override
    public String toString() {
        return get(0,0) + "," + get(0,1) + "," + get(1,0) + "," + get(1,1);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Matrix2x2) && ((Matrix2x2)obj).get(0, 0) == get(0, 0)
                && ((Matrix2x2)obj).get(0, 1) == get(0, 1)
                && ((Matrix2x2)obj).get(1, 0) == get(1, 0)
                && ((Matrix2x2)obj).get(1, 1) == get(1, 1);
    }
    
    /*
    * Representation :
    * [value1,value2]
    * [value3,value4]
    */
    public Matrix2x2(float value1, float value2, float value3, float value4) {
        values = new float[][] {
            { value1, value2 },
            { value3, value4 }
        };
    }
    
    public float get(int row, int col) throws IllegalArgumentException {
        if (row < 0 || row >= 2)
            throw new IllegalArgumentException("row must in [0,1]");
        
        if (col < 0 || col >= 2)
            throw new IllegalArgumentException("col must in [0,1]");
            
        return values[row][col];
    }
        
    /**
     * Static version : creates a duplicate
     * Multiplication term by term
     * @param matrix
     * @param factor
     * @return
     */
    public static Matrix2x2 mult(Matrix2x2 matrix, float factor) {
        Matrix2x2 m = new Matrix2x2(matrix);
        return m.mult(factor);
    }
    
    /**
     * Multiplication term by term
     * @param factor
     * @return
     */
    public Matrix2x2 mult(float factor) {
        for(int i = 0; i < 2; i++)
            for (int j = 0; j < 2; j++)
                values[i][j] *= factor;
        return this;
    }
    
    /**
     * Static version : creates a duplicate
     * Right multiplication by a vector
     * @param matrix
     * @param vector
     * @return
     */
    public static Vector2D mult(Matrix2x2 matrix, Vector2D vector) {
        Vector2D v = new Vector2D(vector);
        return matrix.mult(v);
    }

    /**
     * Right multiplication by a vector
     * @param vector
     * @return
     */
        public Vector2D mult(Vector2D vector) {
        float oldX = vector.getX();
        float oldY = vector.getY();
        
        vector.setX(get(0,0)*oldX+get(0,1)*oldY);
        vector.setY(get(1,0)*oldX+get(1,1)*oldY);
        return vector;
    }

    @Override
    public Object clone() {
        return new Matrix2x2(this);
    }    
}
