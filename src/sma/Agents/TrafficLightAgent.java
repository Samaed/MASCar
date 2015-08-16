package sma.Agents;

import sma.Agents.Behaviors.Lights.LightsConversationBehaviour;
import sma.Agents.Behaviors.Lights.RespondAllBehaviour;
import sma.Model.Light.LightInterface;
import sma.Model.Light.Model;
import sma.Model.Light.State;

/**
 * A TrafficLightAgent is an AbstractAgent implementing a LightInterface
 */
public class TrafficLightAgent extends AbstractAgent implements LightInterface {
    private Model model;
    private boolean waiting, readyConvers, lastGreen;

    @Override
    public boolean isLastGreen() {
        return lastGreen;
    }

    @Override
    public void setLastGreen(boolean lastGreen) {
        this.lastGreen = lastGreen;
    }

    @Override
    public boolean isReadyConvers() {
        return readyConvers;
    }

    @Override
    public void setReadyConvers(boolean readyConvers) {
        this.readyConvers = readyConvers;
    }

    @Override
    public boolean isWaiting() {
        return waiting;
    }
    @Override
    public void setWaiting(boolean waiting) {
        this.waiting = waiting;
    }
    
    @Override
    public void setLightState(State lightState) {
        this.model.setState(lightState);
        addBehaviour(new RespondAllBehaviour());
    }

    public TrafficLightAgent() {
        super(AgentType.LIGHT);
        
        registerO2AInterface(LightInterface.class, this);
        
        waiting = true;
        readyConvers = false;
    }

    /**
     * Initialize the light with the given model
     */
    @Override
    protected void setup() {
        super.setup();
        Object[] args = getArguments();
        
        if(args.length == 2){
            model = (Model) args[0];
            this.setModel(model);
            this.setLastGreen((boolean)args[1]);
            
            addBehaviour(new LightsConversationBehaviour());
        }
    }

    @Override
    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    @Override
    protected void takeDown() {
        super.takeDown();
    }

}
