package sma.Agents.Behaviors.Lights;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
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
import maths.Vector2D;
import sma.Agents.AgentType;
import sma.Agents.ControllerAgent;
import sma.Agents.Ontologie;
import sma.Agents.TrafficLightAgent;
import sma.Model.Controller.ControllerInterface;
import sma.Model.Light.LightInterface;
import sma.Model.Light.Model;
import sma.Model.Map.EndPoints;

/**
 * Behavior used between lights to communicate
 * Allow the master to slave communication and the master to master negociation
 */
public class LightsConversationBehaviour extends CyclicBehaviour {

    private boolean accepted;
    private int nbCars;

    public LightsConversationBehaviour() {
        accepted = false;
    }

    @Override
    public void action() {
        Model model = ((LightInterface) myAgent).getModel();
        LightInterface li = ((LightInterface)myAgent);
        if (model.isMaster() && li.isWaiting() && everyoneIsWaiting(model)) {
            li.setWaiting(false);
            li.setReadyConvers(false);
            AID aid = getFacedLight(model);
            this.nbCars = getNumberOfCarsWaitingInDirection(Vector2D.mult(li.getModel().getWay().getOrigin(), -1));
            if (aid != null) {
                sendMessageNbCar(aid);
            } else {
                conversation(this.nbCars);
            }
        }
        
        if(model.isMaster() && getOtherMasterLight(model).getLocalName().compareTo(myAgent.getAID().getLocalName()) < 0){
            if(otherMasterReadyConvers()) {
                if(!li.isWaiting() && li.isReadyConvers()){
                    li.setReadyConvers(false);
                    conversation(this.nbCars);
                }
            }
        }
        
        
        ACLMessage msg = myAgent.receive();
        if (msg != null) {
            if (msg.getPerformative() == ACLMessage.REQUEST && msg.getOntology().equals(Ontologie.LIGHT_NB_CARS.toString())) {
                if (msg.getContent().equals(MessagesTypes.GET_NB_CARS.toString())) {
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setOntology(Ontologie.LIGHT_NB_CARS.toString());
                    this.nbCars = getNumberOfCarsWaitingInDirection(Vector2D.mult(li.getModel().getWay().getOrigin(), -1));
                    reply.setContent(String.valueOf(this.nbCars));
                    myAgent.send(reply);
                }
            } else if (msg.getPerformative() == ACLMessage.INFORM && msg.getOntology().equals(Ontologie.LIGHT_NB_CARS.toString())) {
                if (msg.getContent() != null) {
                    int nbCars = Integer.valueOf(msg.getContent());
                    this.nbCars += nbCars;
                    li.setReadyConvers(true);
                }
            } else if (msg.getPerformative() == ACLMessage.PROPOSE && msg.getOntology().equals(Ontologie.LIGHT_COMP_NB_CARS.toString())) {
                if (msg.getContent() != null) {
                    li.setReadyConvers(false);
                    int nbOtherCars = Integer.valueOf(msg.getContent());
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                    reply.setOntology(Ontologie.LIGHT_COMP_NB_CARS.toString());
                    if(nbOtherCars <= this.nbCars){
                        long val = map((long)this.nbCars, (long)0, (long)7, (long)(sma.Model.Light.State.Green.getMinDuration()*1000), (long)(sma.Model.Light.State.Green.getMaxDuration()*1000));
                        reply.setContent(String.valueOf((int)val));
                    } else {
                        reply.setContent(String.valueOf((int)(sma.Model.Light.State.Green.getMinDuration()*1000)));
                    }
                    myAgent.send(reply);
                }
            } else if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL && msg.getOntology().equals(Ontologie.LIGHT_COMP_NB_CARS.toString())) {
                if (msg.getContent() != null) {
                    long timeGreen = Long.valueOf(msg.getContent());
                    ACLMessage reply = msg.createReply();
                    reply.addReceiver(myAgent.getAID());
                    reply.addReceiver(getFacedLight(model));
                    AgentController ac;
                    try {
                        ac = myAgent.getContainerController().getAgent(getOtherMasterLight(model).getLocalName());
                        LightInterface li2 = ac.getO2AInterface(LightInterface.class);
                        reply.addReceiver(getFacedLight(li2.getModel()));
                        reply.setPerformative(ACLMessage.CONFIRM);
                        reply.setOntology(Ontologie.LIGHT_COMP_NB_CARS.toString());
                        reply.setContent(String.valueOf(timeGreen));
                        myAgent.send(reply);
                    } catch (ControllerException ex) {
                        Logger.getLogger(LightsConversationBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                }
            } else if (msg.getPerformative() == ACLMessage.CONFIRM && msg.getOntology().equals(Ontologie.LIGHT_COMP_NB_CARS.toString())) {
                if (msg.getContent() != null && !accepted) {
                    accepted = true;
                    long timeGreen = Long.valueOf(msg.getContent());
                    waker(timeGreen);
                }
            } else if (msg.getPerformative() == ACLMessage.INFORM && msg.getOntology().equals(Ontologie.LIGHT_END_CYCLE.toString())) {
                if (msg.getContent() != null) {
                    accepted = false;
                    li.setWaiting(true);
                    li.setReadyConvers(false);
                }
            } else if (msg.getPerformative() == ACLMessage.REQUEST && msg.getOntology().equals(Ontologie.WAITING_CAR.toString())) {
                String title = msg.getContent();
                ACLMessage reply = msg.createReply();
                if (title != null && MessagesTypes.GET_STATE.toString().equals(title)) {
                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setOntology(Ontologie.WAITING_CAR.toString());
                    try {
                        reply.setContentObject(((TrafficLightAgent)myAgent).getModel());
                        myAgent.send(reply);
                    } catch (IOException ex) {
                        Logger.getLogger(LightsConversationBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } else {
            block();
        }
    }
    
    /**
     * Retrieve the number of cars waiting in the given direction from the controller
     * @param direction
     * @return 
     */
    private int getNumberOfCarsWaitingInDirection(Vector2D direction) {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(AgentType.CONTROLLER.toString());
        template.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(myAgent, template);
            if (result.length > 0)
                return myAgent.getContainerController().getAgent(result[0].getName().getLocalName()).getO2AInterface(ControllerInterface.class).getCarsWaitingInDirection(direction);
        } catch (Exception fe) {
            Logger.getLogger(ControllerAgent.class.getName()).log(Level.WARNING, null, fe);
        }
        return 0;
    }
    
    /**
     * Tells if the other master is ready for a talk
     * @return 
     */
    private boolean otherMasterReadyConvers(){
        AgentController ac;
        try {
            ac = myAgent.getContainerController().getAgent(getOtherMasterLight(((LightInterface) myAgent).getModel()).getLocalName());
            LightInterface li2 = ac.getO2AInterface(LightInterface.class);
            if(li2.isReadyConvers() && !li2.isWaiting()){
                return true;
            }
        } catch (ControllerException ex) {
            Logger.getLogger(LightsConversationBehaviour.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    private void conversation(int nbCars){
        AID aid = getOtherMasterLight(((LightInterface) myAgent).getModel());
        if(aid != null){
            sendMessageCompareNbCar(aid, nbCars);
        }
    }
    
    private void waker(long timeGreen){
        LightInterface li = (LightInterface)myAgent;
        if (li.isLastGreen()) {
            myAgent.addBehaviour(new LightWakerBehaviour(myAgent, 1000, true, timeGreen));
        } else {
            myAgent.addBehaviour(new LightWakerBehaviour(myAgent, timeGreen + (long)(sma.Model.Light.State.Orange.getMinDuration()*1000) + 1000, false, 0));
        }
        li.setLastGreen(!li.isLastGreen());
    }
    
    private AID getFacedLight(Model model) {
        List<AID> agents = getLights();
        for (AID aid : agents) {
            try {
                AgentController ac = myAgent.getContainerController().getAgent(aid.getLocalName());
                LightInterface li = ac.getO2AInterface(LightInterface.class);
                if(!li.getModel().isMaster()){
                    if (model.getWay().equals(EndPoints.BOTTOM) && li.getModel().getWay().equals(EndPoints.TOP)) {
                        return aid;
                    } else if (model.getWay().equals(EndPoints.TOP) && li.getModel().getWay().equals(EndPoints.BOTTOM)) {
                        return aid;
                    } else if (model.getWay().equals(EndPoints.RIGHT) && li.getModel().getWay().equals(EndPoints.LEFT)) {
                        return aid;
                    } else if (model.getWay().equals(EndPoints.LEFT) && li.getModel().getWay().equals(EndPoints.RIGHT)) {
                        return aid;
                    }
                }
            } catch (ControllerException ex) {
                Logger.getLogger(LightsConversationBehaviour.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
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
    
    private AID getOtherMasterLight(Model model) {
        List<AID> agents = getLights();
        for (AID aid : agents) {
            try {
                AgentController ac = myAgent.getContainerController().getAgent(aid.getLocalName());
                LightInterface li = ac.getO2AInterface(LightInterface.class);
                if(li.getModel().isMaster()){
                    if ((model.getWay().equals(EndPoints.BOTTOM) || model.getWay().equals(EndPoints.TOP)) 
                            && (li.getModel().getWay().equals(EndPoints.RIGHT) || li.getModel().getWay().equals(EndPoints.LEFT))) {
                        return aid;
                    } else if ((model.getWay().equals(EndPoints.RIGHT) || model.getWay().equals(EndPoints.LEFT)) 
                            && (li.getModel().getWay().equals(EndPoints.BOTTOM) || li.getModel().getWay().equals(EndPoints.TOP))) {
                        return aid;
                    }
                }
            } catch (ControllerException ex) {
                Logger.getLogger(LightsConversationBehaviour.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
    
    private boolean everyoneIsWaiting(Model model) {
        AID aid = getFacedLight(model);
        if(aid == null){
            return false;
        }
        try {
            AgentController ac = myAgent.getContainerController().getAgent(aid.getLocalName());
            LightInterface li = ac.getO2AInterface(LightInterface.class);
            if(li.isWaiting()){
                li.setWaiting(false);
                return true;
            }
        } catch (ControllerException ex) {
            Logger.getLogger(LightsConversationBehaviour.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private void sendMessageNbCar(AID aid) {
        ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
        cfp.addReceiver(aid);
        cfp.setOntology(Ontologie.LIGHT_NB_CARS.toString());
        cfp.setPerformative(ACLMessage.REQUEST);
        cfp.setContent(MessagesTypes.GET_NB_CARS.toString());
        myAgent.send(cfp);
    }
    
    private void sendMessageCompareNbCar(AID aid, int nbCars){
        ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
        cfp.addReceiver(aid);
        cfp.setOntology(Ontologie.LIGHT_COMP_NB_CARS.toString());
        cfp.setPerformative(ACLMessage.PROPOSE);
        cfp.setContent(String.valueOf(nbCars));
        myAgent.send(cfp);
    }
    
    private long map(long x, long in_min, long in_max, long out_min, long out_max)
    {
      return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

}
