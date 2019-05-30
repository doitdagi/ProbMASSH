package it.sh.prob.mas.room.kitchen;

import java.util.List;

import it.sh.prob.mas.ISHSensors;
import it.sh.prob.mas.SHNegotiatorAgent;
import it.sh.prob.mas.room.kitchen.utilities.KitchenSensors;
import it.sh.prob.mas.utilites.AgentID;
import jade.core.AID;

public class KitNegotiatorAgent extends SHNegotiatorAgent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void setup() {
		addSupportedServices(KitchenSensors.getServiceNames());
		addReasoningAblity(); // only if applicable for the agent
		addBehaviour(new RegisterSHServices(toAID(AgentID.HOUSE_DF_AID)));
		addBehaviour(new InitiatorBehaviour());
		addBehaviour(new ParticipantBehaviour());
	}

	@Override
	protected AID getMyReasonerAID() {
		return toAID(AgentID.KITCHEN_REASONER_AID);
	}

	@Override
	protected ISHSensors[] getSupportedServices() {
		return KitchenSensors.values();
	}

	@Override
	protected List<String> getSHService() {
		return serviceList;
	}

	@Override
	protected AID get_Local_df_ID() {
		return toAID(AgentID.KITCHEN_DF_AID);
	}

	@Override
	protected void takeDown() {
		super.takeDown();
		unregisterSHServices(toAID(AgentID.HOUSE_DF_AID));

	}

}
