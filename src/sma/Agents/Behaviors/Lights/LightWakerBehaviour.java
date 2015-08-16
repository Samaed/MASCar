package sma.Agents.Behaviors.Lights;

import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import sma.Agents.Ontologie;
import sma.Model.Light.LightInterface;
import sma.Model.Light.Model;
import sma.Model.Light.State;

/**
 * The behavior used to change the state of the light and manage the time between states
 */
public class LightWakerBehaviour extends WakerBehaviour {
    private final boolean toGreen;
    private final long timeoutGreen;

    public LightWakerBehaviour(Agent a, long timeout, boolean toGreen, long timeoutGreen) {
        super(a, timeout);
        this.toGreen = toGreen;
        this.timeoutGreen = timeoutGreen;
    }
    
    @Override
    protected void handleElapsedTimeout() {
        LightInterface li = (LightInterface)myAgent;
        Model model = li.getModel();
        if(model.getState().equals(sma.Model.Light.State.Green)){
            li.setLightState(State.Orange);
            myAgent.addBehaviour(new LightWakerBehaviour(myAgent, (long)sma.Model.Light.State.Orange.getMinDuration()*1000, false, 0));
        } else if(model.getState().equals(sma.Model.Light.State.Orange)){
            li.setLightState(State.Red);
            endOfCycleMessage();
        } else if(model.getState().equals(sma.Model.Light.State.Red) && toGreen){
            li.setLightState(State.Green);
            myAgent.addBehaviour(new LightWakerBehaviour(myAgent, timeoutGreen, false, 0));
        } else if(model.getState().equals(sma.Model.Light.State.Red) && !toGreen){
            endOfCycleMessage();
        }
    }
    
    private void endOfCycleMessage(){
        ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
        cfp.addReceiver(myAgent.getAID());
        cfp.setOntology(Ontologie.LIGHT_END_CYCLE.toString());
        cfp.setPerformative(ACLMessage.INFORM);
        cfp.setContent("endCycle");
        myAgent.send(cfp);
    }
}
