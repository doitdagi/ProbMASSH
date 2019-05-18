package it.sh.prob.mas.room.bathroom;

import it.sh.prob.mas.ISHSensors;
import it.sh.prob.mas.SHNegotiatorAgent;
import it.sh.prob.mas.room.bathroom.utilites.BathroomSensors;
import jade.core.AID;

//TODO: Negotiation over reasoning tasks
public class BathNegotiatorAgent extends SHNegotiatorAgent {

	private static final AID myReasonerAgent = new AID("BathReasonerAgent", AID.ISLOCALNAME);

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
		return BathroomSensors.values();
	}

}
