package sma.Agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public abstract class AbstractAgent extends Agent {

    /**
     * An AbstractAgent has an AgentType
     * used by the Service Description in the DF
     */
    protected AgentType type;

    public AgentType getType() {
        return type;
    }

    public AbstractAgent(AgentType type) {
        this.type = type;
    }
    
    /**
     * Any AbstractAgent is automatically registered in the DF
     */
    @Override
    protected void setup() {
        registerDF();
    }
    
    /**
     * Register the agent in the DF using its type and its name
     */
    private void registerDF(){
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(this.type.toString());
        sd.setName(getName());
        dfd.addServices(sd);
        try
        {
            DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
        }
    }
    
    /**
     * When the agent is taken down, it is removed from the DF
     */
    @Override
    protected void takeDown() {
        try
        {
            DFService.deregister(this);
        }
        catch (FIPAException fe) {
        }
    }
}
