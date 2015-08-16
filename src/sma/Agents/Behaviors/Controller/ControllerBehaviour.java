package sma.Agents.Behaviors.Controller;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import sma.Agents.ControllerAgent;
import sma.Agents.Ontologie;

/**
 * Behavior used to retrieve messages send to the agent
 */
public class ControllerBehaviour extends CyclicBehaviour {

    @Override
    public void action() {
        ACLMessage msg = myAgent.receive();
        if (msg != null) {
            if (msg.getPerformative() == ACLMessage.INFORM && msg.getOntology().equals(Ontologie.INFORM_TURN.toString())) {
                try {
                    if(msg.getContentObject() != null && msg.getContentObject() instanceof AID)
                    {
                        ACLMessage reply = msg.createReply();
                        AID aid = (AID)msg.getContentObject();
                        reply.setPerformative(ACLMessage.INFORM);
                        reply.setOntology(Ontologie.FRONT_CAR.toString());
                        try {
                            reply.setContentObject(((ControllerAgent)myAgent).getFrontCar(aid));
                            myAgent.send(reply);
                        } catch (IOException ex) {
                            Logger.getLogger(ControllerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } catch (UnreadableException ex) {
                    Logger.getLogger(ControllerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (msg.getPerformative() == ACLMessage.INFORM && msg.getOntology().equals(Ontologie.INFORM_DEATH.toString())) {
                ((ControllerAgent)myAgent).removeAgent(msg.getSender());
            }
        } else {
            block();
        }
    }
    
}
