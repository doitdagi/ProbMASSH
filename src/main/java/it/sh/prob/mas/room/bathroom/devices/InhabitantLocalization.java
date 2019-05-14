package it.sh.prob.mas.room.bathroom.devices;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import it.sh.prob.mas.SHDeviceAgent;
import it.sh.prob.mas.SHParameters;
import it.sh.prob.mas.utilites.InhabitantLocationValues;
import it.sh.prob.mas.utilites.SHServices;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class InhabitantLocalization extends SHDeviceAgent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * List of services that the data generated from this device is relevant 
	 */
	private static List<SHServices> relevantTo = Arrays.asList(SHServices.LIGHT);

	private static final InhabitantLocationValues[] supportedLocations = InhabitantLocationValues.values();
	private static final String PROBLOG_VARIABLE = "location";
	
	@Override
	protected void setup() {
		addBehaviour(new RegisterRelevantSHServices());
		addBehaviour(new HandleLocationRequest());
	}

	
	
	
	
	private class RegisterRelevantSHServices extends OneShotBehaviour{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			registerRelevantSHServices(SHParameters.BATHROOM_LIGHT_SENSOR);
		}
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
		System.out.println("Location value newwwwww");
		return supportedLocations[rnd].toString();
	}
	
}
