package it.sh.prob.mas.room.bedroom;

import it.sh.prob.mas.ISHSensors;
import it.sh.prob.mas.SHNegotiatorAgent;
import it.sh.prob.mas.room.bedroom.utilites.BedroomSensors;
import jade.core.AID;

public class BedNegotiatorAgent extends SHNegotiatorAgent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final AID myReasonerAgent = new AID("BedroomReasonerAgent", AID.ISLOCALNAME);

 

	@Override
	protected void setup() {
		addBehaviour(new RegisterGlobalServices());
		addBehaviour(new InitiatorBehaviour());
		addBehaviour(new ParticipantBehaviour());
	}

	@Override
	protected AID getMyReasonerAID() {
		return myReasonerAgent;
	}

	@Override
	protected ISHSensors[] getSupportedServices() {
		return BedroomSensors.values();
	}

}
