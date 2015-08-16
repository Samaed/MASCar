package sma.Agents.Behaviors.Car;

import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import sma.Agents.AbstractAgent;
import sma.Agents.Ontologie;

/**
 * Behavior called when the car first enter the detection area of the light
 * Add the waiting ontology to the DF
 * Also called when the car stopped waiting
 */
public class WaitingRegistrationBehaviour extends OneShotBehaviour {
    private final boolean register;

    public WaitingRegistrationBehaviour(boolean register) {
        this.register = register;
    }
    
    @Override
    public void action() {
        waitingRegistration(register);
        if(register){
            myAgent.addBehaviour(new AskFirstLightStateBehaviour());
        }
    }
    
    private void waitingRegistration(boolean register){
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(myAgent.getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(((AbstractAgent)myAgent).getType().toString());
        sd.setName(myAgent.getName());
        if(register){
            sd.addOntologies(Ontologie.WAITING_CAR.toString());
        } else {
            sd.removeOntologies(Ontologie.WAITING_CAR.toString());
        }
        dfd.addServices(sd);
        try
        {
            DFService.modify(myAgent, dfd);
        }
        catch (FIPAException fe) {
        }
    }
    
}
