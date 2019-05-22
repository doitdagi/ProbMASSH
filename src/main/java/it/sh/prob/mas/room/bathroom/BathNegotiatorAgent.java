package it.sh.prob.mas.room.bathroom;

import java.util.ArrayList;
import java.util.List;

import it.sh.prob.mas.ISHSensors;
import it.sh.prob.mas.SHNegotiatorAgent;
import it.sh.prob.mas.room.bathroom.utilites.BathroomSensors;
import it.sh.prob.mas.utilites.AgentID;
import jade.core.AID;

//TODO: Negotiation over reasoning tasks
public class BathNegotiatorAgent extends SHNegotiatorAgent {

	private static List<String> services = new ArrayList<String>();

	static {
		for (BathroomSensors sensor : BathroomSensors.values()) {
			services.add(sensor.toString());
		}

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void setup() {
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
		return BathroomSensors.values();
	}

	@Override
	protected List<String> getSHService() {
		return services;
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
