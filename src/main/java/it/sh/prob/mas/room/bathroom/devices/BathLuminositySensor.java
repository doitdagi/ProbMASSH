package it.sh.prob.mas.room.bathroom.devices;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import it.sh.prob.mas.SHDeviceAgent;
import it.sh.prob.mas.SHParameters;
import it.sh.prob.mas.room.bathroom.utilites.BathroomLumValues;
import it.sh.prob.mas.room.bathroom.utilites.BathroomSensors;
import it.sh.prob.mas.utilites.AgentID;
import it.sh.prob.mas.utilites.SHACLProtocolID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class BathLuminositySensor extends SHDeviceAgent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final BathroomLumValues[] lumValues = BathroomLumValues.values();
	private static final String PROBLOG_VARIABLE = "luminosity";

	private static List<String> services = new ArrayList<String>();

	static {
		services.add(SHParameters.LIGHT_SENSOR);
		// To be visible for the negotiator agent
		services.add(BathroomSensors.luminosity.toString());
	}

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
				reply.setProtocol(SHACLProtocolID.MISSINGDATA_INFORM_DEVICE_TO_NEGOTIATOR);
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
	protected List<String> getSHService() {
		return services;
	}

	@Override
	protected void takeDown() {
		super.takeDown();
		unregisterSHServices(toAID(AgentID.BATHROOM_DF_AID));

	}

}
