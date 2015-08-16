package sma.Agents.Behaviors.Car;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import sma.Agents.AgentType;
import sma.Agents.ControllerAgent;
import sma.Agents.Ontologie;

/**
 * Behavior called after the car has turned, tell the other cars and the controller
 * The controller will then change the front car of the car following this one
 */
public class InformTurnBehaviour extends OneShotBehaviour {
    
    @Override
    public void action() {
        sendRequest();
    }
    
    private void sendRequest(){
        sendMessages(getWaitingCarsAndController());
    }
    
    private List<AID> getWaitingCarsAndController(){
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(AgentType.CAR.toString());
        sd.addOntologies(Ontologie.WAITING_CAR.toString());
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
        DFAgentDescription template2 = new DFAgentDescription();
        ServiceDescription sd2 = new ServiceDescription();
        sd2.setType(AgentType.CONTROLLER.toString());
        template2.addServices(sd2);
        try {
            DFAgentDescription[] result = DFService.search(myAgent, template2);
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
        cfp.setOntology(Ontologie.INFORM_TURN.toString());
        cfp.setPerformative(ACLMessage.INFORM);
        try {
            cfp.setContentObject(myAgent.getAID());
        } catch (IOException ex) {
            Logger.getLogger(InformTurnBehaviour.class.getName()).log(Level.SEVERE, null, ex);
        }
        myAgent.send(cfp);
    }
}
