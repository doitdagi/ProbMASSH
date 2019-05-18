package it.sh.prob.mas.room.livingroom;


import it.sh.prob.mas.ISHSensors;
import it.sh.prob.mas.SHNegotiatorAgent;
import it.sh.prob.mas.room.livingroom.utilities.LivingroomSensors;
import jade.core.AID;

public class LRNegotiatorAgent extends SHNegotiatorAgent{

	private static final AID myReasonerAgent = new AID("LRReasonerAgent", AID.ISLOCALNAME);

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
		return LivingroomSensors.values();
	}

}
