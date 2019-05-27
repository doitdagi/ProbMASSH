package it.sh.prob.mas.room.bedroom.devices;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import it.sh.prob.mas.SHDeviceAgent;
import it.sh.prob.mas.SHParameters;
import it.sh.prob.mas.room.bedroom.utilites.BedroomLocations;
import it.sh.prob.mas.room.bedroom.utilites.BedroomSensors;
import it.sh.prob.mas.utilites.AgentID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class BedroomInhabitantLocalization extends SHDeviceAgent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final BedroomLocations[] supportedLocations = BedroomLocations.values();
	private static final String PROBLOG_VARIABLE = "location";

	private static List<String> services = new ArrayList<String>();

	static {
		services.add(SHParameters.LIGHT_SENSOR);
		services.add(BedroomSensors.location.toString());

	}

	@Override
	protected void setup() {
		addBehaviour(new RegisterSHServices(toAID(AgentID.BEDROOM_DF_AID)));
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
		int rnd = new Random().nextInt(supportedLocations.length);
		return supportedLocations[rnd].toString();
	}

	@Override
	protected List<String> getSHService() {
		return services;
	}

	@Override
	protected void takeDown() {
		super.takeDown();
		unregisterSHServices(toAID(AgentID.BEDROOM_DF_AID));

	}

}
