package sma.Agents.Behaviors.Lights;

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
import sma.Model.Light.LightInterface;

/**
 * Behavior used to tell the state of the light to every car waiting for it
 */
public class RespondAllBehaviour extends OneShotBehaviour {

    @Override
    public void action() {
        sendMessages(getWaitingCars());
    }
    
    private List<AID> getWaitingCars(){
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
        return agents;
    }
    
    private void sendMessages(List<AID> agents){
        ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
        for (AID agent : agents) {
            cfp.addReceiver(agent);
        }
        cfp.setOntology(Ontologie.WAITING_CAR.toString());
        cfp.setPerformative(ACLMessage.INFORM);
        try {
            cfp.setContentObject(((LightInterface)myAgent).getModel());
            myAgent.send(cfp);
        } catch (IOException ex) {
            Logger.getLogger(RespondAllBehaviour.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
