package it.sh.prob.mas.room.kitchen.devices;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import it.sh.prob.mas.SHDeviceAgent;
import it.sh.prob.mas.SHParameters;
import it.sh.prob.mas.room.kitchen.utilities.KitchenSensors;
import it.sh.prob.mas.room.kitchen.utilities.KitchenTempValues;
import it.sh.prob.mas.utilites.AgentID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class KitchenTempSensor extends SHDeviceAgent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final KitchenTempValues[] tempValues = KitchenTempValues.values();
	private static final String PROBLOG_VARIABLE = "temperature";

	private static List<String> services = new ArrayList<String>();

	static {
		services.add(SHParameters.TEMPERATURE);
		// To be visible for the negotiator agent
		services.add(KitchenSensors.temperature.toString());
	}

	@Override
	protected void setup() {
		addBehaviour(new RegisterSHServices(toAID(AgentID.KITCHEN_DF_AID)));
		addBehaviour(new HandleTemperatureRequest());
//		addBehaviour(new InformCurrentLumionisity(this, 1000));
	}

	private class HandleTemperatureRequest extends CyclicBehaviour {
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
				reply.setProtocol("fipa-request");
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
		int rnd = new Random().nextInt(tempValues.length);
		return tempValues[rnd].toString();
	}

	@Override
	protected List<String> getSHService() {
		return services;
	}

	@Override
	protected void takeDown() {
		super.takeDown();
		unregisterSHServices(toAID(AgentID.KITCHEN_DF_AID));

	}

}
