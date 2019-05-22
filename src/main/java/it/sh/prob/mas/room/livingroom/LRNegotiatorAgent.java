package it.sh.prob.mas.room.livingroom;

import java.util.ArrayList;
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

	private static List<String> services = new ArrayList<String>();

	static {
		for (LivingroomSensors sensor : LivingroomSensors.values()) {
			services.add(sensor.toString());
		}
	}

	@Override
	protected void setup() {
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
		return services;
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
