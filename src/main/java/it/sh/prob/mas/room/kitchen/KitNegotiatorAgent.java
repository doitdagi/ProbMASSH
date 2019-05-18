package it.sh.prob.mas.room.kitchen;

import it.sh.prob.mas.ISHSensors;
import it.sh.prob.mas.SHNegotiatorAgent;
import it.sh.prob.mas.room.kitchen.utilities.KitchenSensors;
import jade.core.AID;

public class KitNegotiatorAgent extends SHNegotiatorAgent {

	private static final AID myReasonerAgent = new AID("KitReasonerAgent", AID.ISLOCALNAME);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
 

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
		return KitchenSensors.values();
	}

}
