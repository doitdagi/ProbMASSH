package it.sh.prob.mas.room.bathroom;

import java.util.List;

import it.sh.prob.mas.ISHSensors;
import it.sh.prob.mas.SHNegotiatorAgent;
import it.sh.prob.mas.room.bathroom.utilites.BathroomSensors;
import it.sh.prob.mas.utilites.AgentID;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

//TODO: Negotiation over reasoning tasks
public class BathNegotiatorAgent extends SHNegotiatorAgent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void setup() {
		addSupportedServices(BathroomSensors.getServiceNames());
		addReasoningAblity(); //only if applicable for the agent
		addBehaviour(new RegisterSHServices(toAID(AgentID.HOUSE_DF_AID)));
		addBehaviour(new InitiatorBehaviour());
		addBehaviour(new ParticipantBehaviour());
	}

	@Override
	protected AID getMyReasonerAID() {
		return toAID(AgentID.BATHROOM_REASONER_AID);
	}

	@Override
	protected ISHSensors[] getSupportedServices() {
	
	List<AID> reasoners =	getSensorDataProviders("reasoner", myAgent, "home-df");
	
	ACLMessage 
	reasoners.get(0) 
		return BathroomSensors.values();
	}

	@Override
	protected List<String> getSHService() {
		return serviceList;
	}
	
	@Override 
	protected AID get_Local_df_ID() {
		return toAID(AgentID.BATHROOM_DF_AID);
	}

	@Override
	protected void takeDown() {
		super.takeDown();
		unregisterSHServices(toAID(AgentID.HOUSE_DF_AID));
	}

}
