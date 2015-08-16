package sma.Agents.Behaviors.Car;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import sma.Agents.AgentType;
import sma.Agents.Behaviors.Lights.MessagesTypes;
import sma.Agents.ControllerAgent;
import sma.Agents.Ontologie;

/**
 * Behavior used between a car and the lights to get the first state
 * Sends a request to every light
 */
public class AskFirstLightStateBehaviour extends OneShotBehaviour {

    @Override
    public void action() {
        sendFirstRequest();
    }
    
    private void sendFirstRequest(){
        sendMessages(getLights());
    }
    
    private List<AID> getLights(){
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(AgentType.LIGHT.toString());
        template.addServices(sd);
        List<AID> agents = new ArrayList<>();
        try {
            DFAgentDescription[] result = DFService.search(myAgent, template);
            for (int i = 0; i < result.length; ++i) {
                agents.add(result[i].getName());
            }
        } catch (FIPAException fe) {
            Logger.getLogger(ControllerAgent.class.getName()).log(Level.WARNING, null, fe);
        }
        return agents;
    }
    
    private void sendMessages(List<AID> agents){
        ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
        for (AID agent : agents) {
            cfp.addReceiver(agent);
        }
        cfp.setOntology(Ontologie.WAITING_CAR.toString());
        cfp.setPerformative(ACLMessage.REQUEST);
        cfp.setContent(MessagesTypes.GET_STATE.toString());
        myAgent.send(cfp);
    }
}
