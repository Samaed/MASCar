package sma.Agents.Behaviors.Car;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import sma.Agents.AgentType;
import sma.Agents.ControllerAgent;
import sma.Agents.Ontologie;
import sma.Model.Car.CarInterface;
import sma.Model.Car.Destinations;
import sma.Model.Light.Model;
import sma.Model.Light.State;
import sma.Model.Map.EndPoints;
import utils.Constants;
import utils.Utils;

/**
 * Continuous behavior used to get messages
 */
public class CarMessagesBehaviour extends CyclicBehaviour {    
    
    @Override
    public void action() {
        ACLMessage msg = myAgent.receive();
        if (msg != null) {
            if (msg.getPerformative() == ACLMessage.INFORM && Ontologie.FRONT_CAR.toString().equals(msg.getOntology())) {
                try {
                    if (msg.getContentObject() != null && msg.getContentObject() instanceof AID) {
                        AID response = (AID) msg.getContentObject();
                        CarInterface ci = (CarInterface) myAgent;
                        ci.setFrontCar(response);
                    }
                } catch (UnreadableException ex) {
                    Logger.getLogger(CarMessagesBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (msg.getPerformative() == ACLMessage.INFORM && msg.getOntology().equals(Ontologie.WAITING_CAR.toString())) {
                try {
                    if (msg.getContentObject() != null && msg.getContentObject() instanceof Model) {
                        Model lightModel = (Model) msg.getContentObject();
                        CarInterface ci = (CarInterface) myAgent;
                        if (ci.getStartPoint().equals(lightModel.getWay()) && !(lightModel.getState().equals(State.Red))) {
                            ci.setRedLightAhead(false);
                            act();
                        } else if (ci.getStartPoint().equals(lightModel.getWay())) {
                            ci.setRedLightAhead(true);
                        }
                    }
                } catch (UnreadableException ex) {
                    Logger.getLogger(CarMessagesBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (msg.getPerformative() == ACLMessage.INFORM && msg.getOntology().equals(Ontologie.INFORM_DESTINATION.toString())) {
                try {
                    if (msg.getContentObject() != null && msg.getContentObject() instanceof Destinations) {
                        Destinations dest = (Destinations) msg.getContentObject();
                        CarInterface ci = (CarInterface) myAgent;
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                act();
                            }
                        }, Constants.REACTION_TIME);
                    }
                } catch (UnreadableException ex) {
                    Logger.getLogger(CarMessagesBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (msg.getPerformative() == ACLMessage.INFORM && msg.getOntology().equals(Ontologie.INFORM_TURN.toString())) {
                CarInterface ci = (CarInterface) myAgent;
                act();
            }
        } else {
            block();
        }
    }
    
    /**
     * Acts judging from the state (engage or not)
     */
    private void act() {
        CarInterface ci = (CarInterface) myAgent;
        if (!ci.getRedLightAhead() && !anyCrossingCar()) {
            ci.setEngaged();
        } else {
            myAgent.addBehaviour(new CrossroadBehaviour());
        }
    }

    /**
     * Tells if any car is going to across the way
     * @return 
     */
    private boolean anyCrossingCar() {        
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(AgentType.CAR.toString());
        sd.addOntologies(Ontologie.WAITING_CAR.toString());
        template.addServices(sd);
        CarInterface ci = (CarInterface) myAgent;
        
        if (!ci.getDestination().equals(Destinations.LEFT)) return false;
        
        List<AID> agents = new ArrayList<>();
        try {
            DFAgentDescription[] result = DFService.search(myAgent, template);
            for (int i = 0; i < result.length; ++i) {
                AgentController ac;
                try {
                    ac = myAgent.getContainerController().getAgent(result[i].getName().getLocalName());
                    CarInterface ci2 = Utils.getCarInterface(ac);
                    if (!result[i].getName().equals(myAgent.getAID()) && facedCar(ci, ci2) && ci2.getDestination().equals(Destinations.FORWARD)) {
                        return true;
                    }
                } catch (ControllerException ex) {
                    Logger.getLogger(CarMessagesBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (FIPAException fe) {
            Logger.getLogger(ControllerAgent.class.getName()).log(Level.WARNING, null, fe);
        }
        return false;
    }
    
    /**
     * Tells if the given car is facing the first one or not
     * @param ci
     * @param ci2
     * @return
     */
    public static boolean facedCar(CarInterface ci, CarInterface ci2){
        if (ci == null || ci2 == null) return false;
        
        EndPoints ed = ci.getStartPoint();
        EndPoints ed2 = ci2.getStartPoint();
        if(ed.equals(EndPoints.BOTTOM) && ed2.equals(EndPoints.TOP)){
            return true;
        } else if(ed.equals(EndPoints.TOP) && ed2.equals(EndPoints.BOTTOM)){
            return true;
        } else if(ed.equals(EndPoints.LEFT) && ed2.equals(EndPoints.RIGHT)){
            return true;
        } else if(ed.equals(EndPoints.RIGHT) && ed2.equals(EndPoints.LEFT)){
            return true;
        }
        return false;
    }
}
