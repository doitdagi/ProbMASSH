package it.sh.prob.mas.room.bathroom.devices;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import it.sh.prob.mas.SHDeviceAgent;
import it.sh.prob.mas.utilites.InhabitantActivitityValues;
import it.sh.prob.mas.utilites.SHServices;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class InhabitantActivityRecognition extends SHDeviceAgent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final InhabitantActivitityValues[] supportedActivities = InhabitantActivitityValues.values();

	private static final String PROBLOG_VARIABLE = "activity";
	
	/**
	 * List of services that the data generated from this device is relevant
	 */
	private static List<SHServices> relevantTo = Arrays.asList(SHServices.LIGHT);

	@Override
	protected void setup() {
		addBehaviour(new RegisterRelevantSHServices());
		addBehaviour(new HandleActivityRequest());
	}

	private class RegisterRelevantSHServices extends OneShotBehaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			registerRelevantSHServices(getAID(), relevantTo);
		}
	}

	private class HandleActivityRequest extends CyclicBehaviour {
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

	/**
	 * Generate inhabitant activity randomly from the lists of supported activities
	 * in this particular environment
	 * 
	 * @return
	 */
	@Override
	protected String generateRandomDeviceValues() {
		int rnd = new Random().nextInt(supportedActivities.length);
		return supportedActivities[rnd].toString();
	}
}
