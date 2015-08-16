package utils;

import jade.core.AID;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import sma.Model.Car.CarInterface;
import sma.Model.Light.LightInterface;

/**
 * Utils functions
 */
public class Utils {
    
    public static AID getAgentAID(AgentController ac) {
        if (ac == null) return null;
        try { return new AID(ac.getName(), AID.ISLOCALNAME); } catch (StaleProxyException ex) { return null; }  
    }
    
    public static CarInterface getCarInterface(AgentController ac) {
        try {
            return ac.getO2AInterface(CarInterface.class);
        } catch (StaleProxyException ex) {
            return null;
        }
    }
    
    public static CarInterface getCarInterface(AgentContainer acnt, AID aid) {
        if (aid == null) return null;
        
        try {
            return getCarInterface(acnt.getAgent(aid.getLocalName(), true));
        } catch (Exception ex) {
            return null;
        }
    }
    
    public static LightInterface getLightInterface(AgentController ac) {
        try {
            return ac.getO2AInterface(LightInterface.class);
        } catch (StaleProxyException ex) {
            return null;
        }
    }
}
