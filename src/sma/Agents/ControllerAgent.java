package sma.Agents;

import sma.Agents.Behaviors.Controller.ControllerBehaviour;
import jade.core.AID;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import maths.Vector2D;
import sma.Model.Car.CarInterface;
import sma.Model.Controller.ControllerInterface;
import sma.Model.Light.LightInterface;
import sma.Model.Light.Model;
import sma.Model.Light.State;
import sma.Model.Map.EndPoints;
import sma.gui.MainWindow;
import utils.BiHashMap;
import utils.Utils;
import utils.Constants;

/**
 * A ControllerAgent is an AbstractAgent of type CONTROLLER implementing a ControllerInterface
 */
public class ControllerAgent extends AbstractAgent implements ControllerInterface {
    
    /**
     * We use an UID to make unique agent names
     */
    private static int UID = 0;
    
    /**
     * The cars and lights are stocked so we can apply logic onto them
     */
    private final HashMap<AID, AgentController> carAgents = new HashMap<>();
    private final HashMap<AID, AgentController> lightAgents = new HashMap<>();
    /**
     * We keep a Bidirectional HashMap of the cars in front of others
     * To update it quickly (O(1) complexity)
     */
    private final BiHashMap<AID, AID> carsFront = new BiHashMap<>();

    public ControllerAgent() {
        super(AgentType.CONTROLLER);
    }
    
    /**
     * Initialize the ControllerAgent
     */
    @Override
    protected void setup() {
        super.setup();
        
        // The creating of the MainWindow is done with the Controller
        MainWindow.instance(this);

        // Spawn cars at constant rate if not colliding
        new Timer().scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run() {
                spawnCar();
            }
        }, 0, Constants.SPAWN_PERIOD);
        
        // We set the lights of the simulation        
        createLightAgent(new Model(State.Red, EndPoints.BOTTOM, true), false);
        createLightAgent(new Model(State.Red, EndPoints.TOP, false), false);
        createLightAgent(new Model(State.Red, EndPoints.LEFT, true), true);
        createLightAgent(new Model(State.Red, EndPoints.RIGHT, false), true);
        
        registerO2AInterface(ControllerInterface.class, this);
        
        addBehaviour(new ControllerBehaviour());
    }
    
    /**
     * @return the lights as a List<LightInterface>
     */
    public synchronized List<LightInterface> getLights() {
        return lightAgents.values().stream().map(ac -> Utils.getLightInterface(ac)).collect(Collectors.toList());
    }
    
    /**
     * @return the cars as a List<CarInterface>
     */
    public synchronized List<CarInterface> getCars() {
        return carAgents.values().stream().map(ac -> Utils.getCarInterface(ac)).collect(Collectors.toList());
    }
    
    /**
     * Called when an agent tells he is dead
     * Removes it from the different collections
     * @param aid
     */
    public void removeAgent(AID aid) {
        carAgents.remove(aid);
        AID following = carsFront.getByValue(aid);
        if (following != null)
            carsFront.put(following, getFrontCar(following));
    }
    
    /**
     * Computes the front car for a given car
     * @param aid
     * @param position
     * @param direction
     * @return
     */
    public AID getFrontCar(AID aid, Vector2D position, Vector2D direction) {
        CarInterface carInterface;
        
        AgentController bestChoice = null;
        boolean xDependent = (direction.getX() != 0);
        int sign = (int)((xDependent) ? direction.getX() : direction.getY());
        float best = sign*Float.POSITIVE_INFINITY;
        float currentPosition;
        float myPosition = (xDependent) ? position.getX() : position.getY();
        
        HashMap<AgentController, CarInterface> carsInDirection = getCarsInDirection(direction);
        for (Entry<AgentController, CarInterface> entrySet : carsInDirection.entrySet()) {
            carInterface = entrySet.getValue();
            switch (sign) {
                case -1:
                    currentPosition = (xDependent) ? carInterface.getPosition().getX() : carInterface.getPosition().getY();
                    if (currentPosition > best && currentPosition < myPosition) {
                        best = currentPosition;
                        bestChoice = entrySet.getKey();
                    }
                    break;
                case 1:
                    currentPosition = (xDependent) ? carInterface.getPosition().getX() : carInterface.getPosition().getY();
                    if (currentPosition < best && currentPosition > myPosition) {
                        best = currentPosition;
                        bestChoice = entrySet.getKey();
                    }
                    break;
            }
        }
        
        AID otherAID = (bestChoice != null) ? Utils.getAgentAID(bestChoice) : null;
        carsFront.put(aid, otherAID);
        
        return otherAID;
    }
    
    public AID getFrontCar(AID aid, sma.Model.Car.Model m) {
        return getFrontCar(aid, m.getPosition(), m.getDirection());
    }
    
    public AID getFrontCar(AID aid) { 
        CarInterface car = null;
        try {
            car = Utils.getCarInterface(getContainerController().getAgent(aid.getLocalName()));
        } catch (ControllerException ex) {
        }
        
        return (car != null) ? getFrontCar(aid, car.getPosition(), car.getDirection()) : null;
    }
    
    /**
     * Spawn a car if not colliding
     */
    public void spawnCar() {
        spawnCar(false);
    }
    
    /**
     * Spawn a car
     * @param ifColliding
     */
    public void spawnCar(boolean ifColliding) {
        sma.Model.Car.Model m = new sma.Model.Car.Model();
        if (ifColliding || isColliding(m)) {
            createCarAgent(m);
        }
    }
    
    /**
     * Computes if a model will cause a car to be colliding
     * @param m
     * @return
     */
    public boolean isColliding(sma.Model.Car.Model m) {
        HashMap<AgentController, CarInterface> cars = getCarsInDirection(m.getDirection());
        Vector2D position = m.getPosition();
        return !cars.values().stream().anyMatch(car -> car.getPosition().equals(position,Constants.SAFETY_DISTANCE));
    }

    /**
     * Return the cars in the given direction
     * Cars that have already turned are counted
     * @param direction
     * @return
     */
    public HashMap<AgentController, CarInterface> getCarsInDirection(Vector2D direction) {
        return getCarsInDirection(direction, true);
    }
    
    public HashMap<AgentController, CarInterface> getCarsInDirection(Vector2D direction, boolean countTurned) {
        HashMap<AgentController, CarInterface> cars = new HashMap<>(carAgents.size());
        CarInterface carInterface;
        for (AgentController car : carAgents.values()) {
            carInterface = Utils.getCarInterface(car);
            if (carInterface != null && carInterface.getDirection().equals(direction) && (countTurned || !carInterface.hasTurned()))
                cars.put(car, carInterface);
        }
        return cars;
    }

    
    /**
     * Returns the number of car in a direction
     * Cars that have already turned are not counted
     * @param direction
     * @return
     */
    @Override
    public int getCarsWaitingInDirection(Vector2D direction) {
        return getCarsInDirection(direction, false).size();
    }
    
    /**
     * Creates the car associated with the model and store it
     * @param model
     */
    public void createCarAgent(sma.Model.Car.Model model) {
        String name = getUniqueName("CarAgent");
        AID aid = new AID(name,AID.ISLOCALNAME);
        model.setFrontCar(getFrontCar(aid, model));
        carAgents.put(aid, createAgent("CarAgent", new Object[] { model }, name));
    }

    /**
     * Creates the light associated with the model and store it
     * @param model
     * @param first
     */
    public void createLightAgent(Model model, boolean first) {
        String name = getUniqueName("TrafficLightAgent");
        AID aid = new AID(name,AID.ISLOCALNAME);
        lightAgents.put(aid, createAgent("TrafficLightAgent", new Object[] { model, first }, name)); 
    }
    
    /**
     * Create a agent based on the type and the model passed
     * @param type
     * @param args
     * @return
     */
    public AgentController createAgent(String type, Object[] args) {
        return createAgent(type, args, null);
    }
    
    public AgentController createAgent(String type, Object[] args, String name) {
        ContainerController cc = getContainerController();
        AgentController ac = null;
        
        try {
            ac = cc.createNewAgent((name != null) ? name : getUniqueName(type), Constants.AGENTS_PACKAGE+type, args);
            ac.start();
        } catch (StaleProxyException ex) {
            Logger.getLogger(ControllerAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return ac;
    }
    
    /**
     * Returns a unique name for the agent
     * @param type
     * @return 
     */
    private String getUniqueName(String type) {
        return type+UID++;
    }
}
