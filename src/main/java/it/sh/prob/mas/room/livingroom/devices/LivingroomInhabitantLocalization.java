package it.sh.prob.mas.room.livingroom.devices;

import java.util.Random;

import it.sh.prob.mas.SHDeviceAgent;
import it.sh.prob.mas.SHParameters;
import it.sh.prob.mas.room.livingroom.utilities.LivingroomLocations;
import it.sh.prob.mas.utilites.AgentID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class LivingroomInhabitantLocalization extends SHDeviceAgent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final LivingroomLocations[] supportedLocations = LivingroomLocations.values();
	private static final String PROBLOG_VARIABLE = "location";
	
	@Override
	protected void setup() {
		addBehaviour(new RegisterSHServices(toAID(AgentID.LIVINGROOM_DF_AID)));
		addBehaviour(new HandleLocationRequest());
	}

	
	private class HandleLocationRequest extends CyclicBehaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage msg = receive(mt);
			if (msg != null) {
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.INFORM);
				reply.setContent(formulateReply(PROBLOG_VARIABLE));
				myAgent.send(reply);
			} else {
				block();
			}
		}

	}


	@Override
	protected String generateRandomDeviceValues() {
		int rnd = new Random().nextInt(supportedLocations.length);
		return supportedLocations[rnd].toString();
	}


	@Override
	protected String getSHService() {
		return SHParameters.LIGHT_SENSOR;
	}
	
}
