package sma.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.HashMap;
import javax.imageio.ImageIO;
import maths.Vector2D;
import sma.Model.Car.CarInterface;
import sma.Model.Car.Destinations;
import sma.Model.Light.LightInterface;
import sma.Model.Light.State;
import sma.Model.Map.EndPoints;
import utils.Constants;

/**
 *
 */
public class GUIUtils {
    
    private static GUIUtils INSTANCE = null;
    
    private final HashMap<State, Image> lights = new HashMap<>();
    private final HashMap<EndPoints, Image> directions = new HashMap<>();
    
    private GUIUtils() {
        lights.put(State.Red, loadImage("Red"));
        lights.put(State.Orange, loadImage("Orange"));
        lights.put(State.Green, loadImage("Green"));
        directions.put(EndPoints.BOTTOM, loadImage("Bottom"));
        directions.put(EndPoints.LEFT, loadImage("Left"));
        directions.put(EndPoints.TOP, loadImage("Top"));
        directions.put(EndPoints.RIGHT, loadImage("Right"));
    }
    
    /**
     *
     * @return
     */
    public static synchronized GUIUtils getInstance() {
        if (INSTANCE == null)
            INSTANCE = new GUIUtils();
        return INSTANCE;
    }
    
    /**
     *
     * @param name
     * @return
     */
    public final Image loadImage(String name) {
        Image image = null;
        try {
            image = ImageIO.read(getClass().getClassLoader().getResource(String.format("%s/%s.%s", Constants.IMAGE_FOLDER, name, Constants.IMAGE_FORMAT)));
        } catch(Exception ex) {
        }
        return image;
    }
    
    /**
     *
     * @param g2
     * @param halfSize
     * @param car
     */
    public void drawCar(Graphics2D g2, Rectangle halfSize, CarInterface car) {
         if (car == null) return;

        Vector2D origin = car.getStartPoint().getOrigin();
        boolean rotate = false;
        boolean hasTurned = car.hasTurned();
        Dimension offset = new Dimension();
        Destinations destination = car.getDestination();
        Vector2D direction = car.getDirection();

        if (origin.equals(EndPoints.TOP.getOrigin())) {
            offset.setSize(-20, -40);
            if (destination.equals(Destinations.RIGHT) && hasTurned) {
                offset.setSize(-20, -10);
            } else if (destination.equals(Destinations.LEFT) && hasTurned) {
                offset.setSize(-20, 10);
            }
        } else if (origin.equals(EndPoints.LEFT.getOrigin())) {
            offset.setSize(-40, 10);
            if (destination.equals(Destinations.RIGHT) && hasTurned) {
                offset.setSize(-10, 6);
            } else if (destination.equals(Destinations.LEFT) && hasTurned) {
                offset.setSize(18, 0);
            }
        } else if (origin.equals(EndPoints.BOTTOM.getOrigin())) {
            offset.setSize(10, 20);
            if (destination.equals(Destinations.RIGHT) && hasTurned) {
                offset.setSize(0, 0);
            } else if (destination.equals(Destinations.LEFT) && hasTurned) {
                offset.setSize(0, -26);
            }
        } else if (origin.equals(EndPoints.RIGHT.getOrigin())) {
            offset.setSize(20, -20);
            if (destination.equals(Destinations.RIGHT) && hasTurned) {
                offset.setSize(0, -24);
            } else if (destination.equals(Destinations.LEFT) && hasTurned) {
                offset.setSize(-26, -24);
            }
        }
        
        if (direction.equals(EndPoints.BOTTOM.getOrigin()) || direction.equals(EndPoints.TOP.getOrigin())) {
            rotate = true;
        } else {
            rotate = false;
        }
        
        int carX = (int)(car.getPosition().getX()*halfSize.width)+halfSize.width+offset.width;
        int carY = (int)(-car.getPosition().getY()*halfSize.height)+halfSize.height+offset.height;
        
        Color carColor = car.getColor();
        
        g2.setColor(carColor);
        g2.fillRect(carX, carY, !rotate ? 20 : 10, !rotate ? 10 : 20);
        
        g2.setColor(new Color(255-carColor.getRed(),255-carColor.getGreen(),255-carColor.getBlue()));
        
        int deltaX = 0, deltaY = 0;
        if (destination.equals(Destinations.RIGHT) && !hasTurned && RoadPanel.FRAMECOUNTER < Constants.FRAMERATE/2) {
            if (origin.equals(EndPoints.RIGHT.getOrigin())) {
                deltaX = 0; deltaY = 0;
            } else if (origin.equals(EndPoints.LEFT.getOrigin())) {
                deltaX = 15; deltaY = 5;
            } else if (origin.equals(EndPoints.BOTTOM.getOrigin())) {
                deltaX = 5; deltaY = 0;
            } else if (origin.equals(EndPoints.TOP.getOrigin())) {
                deltaX = 0; deltaY = 15;
            }
            g2.fillRect(carX+deltaX, carY+deltaY, 5, 5);
        } else if (destination.equals(Destinations.LEFT) && !hasTurned && RoadPanel.FRAMECOUNTER < Constants.FRAMERATE/2) {
            if (origin.equals(EndPoints.RIGHT.getOrigin())) {
                deltaX = 0; deltaY = 5;
            } else if (origin.equals(EndPoints.LEFT.getOrigin())) {
                deltaX = 15; deltaY = 0;
            } else if (origin.equals(EndPoints.BOTTOM.getOrigin())) {
                deltaX = 0; deltaY = 0;
            } else if (origin.equals(EndPoints.TOP.getOrigin())) {
                deltaX = 5; deltaY = 15;
            }
            g2.fillRect(carX+deltaX, carY+deltaY, 5, 5);
        }
    }
    
    /**
     *
     * @param g2
     * @param halfSize
     * @param light
     */
    public void drawLight(Graphics2D g2, Rectangle halfSize, LightInterface light) {
        if (light.getModel() == null)
            return;
       
        EndPoints way = light.getModel().getWay();
        Dimension offset = new Dimension();
        
        if (way.getOrigin().equals(EndPoints.BOTTOM.getOrigin())) {
            offset.setSize(-35, -55);
        } else if (way.getOrigin().equals(EndPoints.RIGHT.getOrigin())) {
            offset.setSize(-35, 35);
        } else if (way.getOrigin().equals(EndPoints.TOP.getOrigin())) {
            offset.setSize(25, 35);
        } else if (way.getOrigin().equals(EndPoints.LEFT.getOrigin())) {
            offset.setSize(25, -55);
        }
        
        if (directions.get(way) != null)
            g2.drawImage(directions.get(way), halfSize.width+offset.width+1, halfSize.height+offset.height-12, null);
        
        if (lights.get(light.getModel().getState()) != null) {
            g2.drawImage(lights.get(light.getModel().getState()),
                    halfSize.width+offset.width, halfSize.height+offset.height, null);
        }
    }
    
}
