package it.sh.prob.mas.room.bedroom;

import java.util.ArrayList;
import java.util.List;

import it.sh.prob.mas.ISHSensors;
import it.sh.prob.mas.SHNegotiatorAgent;
import it.sh.prob.mas.room.bedroom.utilites.BedroomSensors;
import it.sh.prob.mas.utilites.AgentID;
import jade.core.AID;

public class BedNegotiatorAgent extends SHNegotiatorAgent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private static List<String> services = new ArrayList<String>();
	
	static {
		for (BedroomSensors sensor : BedroomSensors.values()) {
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
		return toAID(AgentID.BEDROOM_REASONER_AID);
	}
 
	@Override
	protected ISHSensors[] getSupportedServices() {
		return BedroomSensors.values();
	}

	@Override
	protected List<String> getSHService() {
		return services;
	}
	
	@Override 
	protected AID get_Local_df_ID() {
		return toAID(AgentID.BEDROOM_DF_AID);
	}
	 @Override
	 protected void takeDown() {
	 	super.takeDown();
	 	unregisterSHServices(toAID(AgentID.HOUSE_DF_AID));
	  
	  }
}
