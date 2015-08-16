package sma.Agents.Behaviors.Car;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import sma.Agents.AgentType;
import sma.Agents.Behaviors.Lights.RespondAllBehaviour;
import sma.Agents.ControllerAgent;
import sma.Agents.Ontologie;
import sma.Model.Car.CarInterface;

/**
 * Behavior applied when a car can't turn immediately, send the goal of the car to the others
 */
public class CrossroadBehaviour extends OneShotBehaviour {

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
        CarInterface ci = (CarInterface) myAgent;
        List<AID> agents = new ArrayList<>();
        try {
            DFAgentDescription[] result = DFService.search(myAgent, template);
            for (int i = 0; i < result.length; ++i) {
                AgentController ac;
                try {
                    ac = myAgent.getContainerController().getAgent(result[i].getName().getLocalName());
                    CarInterface ci2 = ac.getO2AInterface(CarInterface.class);
                    if (!result[i].getName().equals(myAgent.getAID()) && CarMessagesBehaviour.facedCar(ci, ci2)) {
                        agents.add(result[i].getName());
                    }
                } catch (ControllerException ex) {
                    Logger.getLogger(CarMessagesBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                }
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
        cfp.setOntology(Ontologie.INFORM_DESTINATION.toString());
        cfp.setPerformative(ACLMessage.INFORM);
        try {
            cfp.setContentObject(((CarInterface)myAgent).getDestination());
            myAgent.send(cfp);
        } catch (IOException ex) {
            Logger.getLogger(RespondAllBehaviour.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


}
