package it.sh.prob.mas.room.livingroom;

import java.util.List;

import it.sh.prob.mas.ISHSensors;
import it.sh.prob.mas.SHNegotiatorAgent;
import it.sh.prob.mas.room.livingroom.utilities.LivingroomSensors;
import it.sh.prob.mas.utilites.AgentID;
import jade.core.AID;

public class LRNegotiatorAgent extends SHNegotiatorAgent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
 
	@Override
	protected void setup() {
		addSupportedServices(LivingroomSensors.getServiceNames());
		addReasoningAblity(); //only if applicable for the agent
		addBehaviour(new RegisterSHServices(toAID(AgentID.HOUSE_DF_AID)));
		addBehaviour(new InitiatorBehaviour());
		addBehaviour(new ParticipantBehaviour());
	}

	@Override
	protected AID getMyReasonerAID() {
		return toAID(AgentID.LIVINGROOM_REASONER_AID);
	}

	@Override
	protected ISHSensors[] getSupportedServices() {
		return LivingroomSensors.values();
	}

	@Override
	protected List<String> getSHService() {
		return serviceList;
	}

	@Override
	protected AID get_Local_df_ID() {
		return toAID(AgentID.LIVINGROOM_DF_AID);
	}

	@Override
	protected void takeDown() {
		super.takeDown();
		unregisterSHServices(toAID(AgentID.HOUSE_DF_AID));

	}

}
