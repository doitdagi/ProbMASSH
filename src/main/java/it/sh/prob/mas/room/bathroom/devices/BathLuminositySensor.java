package it.sh.prob.mas.room.bathroom.devices;

import java.util.Random;

import it.sh.prob.mas.SHDeviceAgent;
import it.sh.prob.mas.SHParameters;
import it.sh.prob.mas.room.bathroom.utilites.BathroomLumValues;
import it.sh.prob.mas.utilites.AgentID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class BathLuminositySensor extends SHDeviceAgent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final BathroomLumValues[] lumValues = BathroomLumValues.values();
	private static final String PROBLOG_VARIABLE = "luminicity";
	@Override
	protected void setup() {
		addBehaviour(new RegisterSHServices(toAID(AgentID.BATHROOM_DF_AID)));
		addBehaviour(new HandleLuminosityRequest());
//		addBehaviour(new InformCurrentLumionisity(this, 1000));
	}
 
	

	private class HandleLuminosityRequest extends CyclicBehaviour {
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
		int rnd = new Random().nextInt(lumValues.length);
		return lumValues[rnd].toString();
	}

	@Override
	protected String getSHService() {
		return SHParameters.LIGHT_SENSOR;
	}



}
