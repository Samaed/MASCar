package sma.Agents.Behaviors.Car;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import java.util.logging.Level;
import java.util.logging.Logger;
import sma.Agents.AgentType;
import sma.Agents.ControllerAgent;
import sma.Agents.Ontologie;

/**
 * Behavior called when the car is going to suicide (outside of the simulation)
 * Tells the controller that the car is going to die
 */
public class TellDeathManagerBehaviour extends OneShotBehaviour {

    @Override
    public void action() {
        sendMessage(getManager());
        myAgent.doDelete();
    }
    
    private AID getManager(){
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(AgentType.CONTROLLER.toString());
        template.addServices(sd);
        AID agent = null;
        try {
            DFAgentDescription[] result = DFService.search(myAgent, template);
            agent = result[0].getName();
        } catch (FIPAException fe) {
            Logger.getLogger(ControllerAgent.class.getName()).log(Level.WARNING, null, fe);
        }
        return agent;
    }
    
    private void sendMessage(AID aid) {
        if (aid == null) return;
        
        ACLMessage inf = new ACLMessage(ACLMessage.INFORM);
        inf.addReceiver(aid);
        inf.setOntology(Ontologie.INFORM_DEATH.toString());
        myAgent.send(inf);
    }
}
