package it.sh.prob.mas.room.bathroom.devices;

import it.sh.prob.mas.SHDeviceAgent;
import it.sh.prob.mas.SHParameters;
import it.sh.prob.mas.room.bathroom.utilites.BathroomLights;
import it.sh.prob.mas.utilites.AgentID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class BathLightController extends SHDeviceAgent {

	private BathroomLights currentLight;


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void setup() {
		addBehaviour(new RegisterSHServices(toAID(AgentID.BATHROOM_DF_AID)));
		addBehaviour(new ChangeAcutatorStateRequest());
		addBehaviour(new HandleCurrentAcutatorStateRequest());
	}

	private class ChangeAcutatorStateRequest extends CyclicBehaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM), MessageTemplate.MatchSender(toAID(AgentID.BATHROOM_REASONER_AID)));
			ACLMessage msg = myAgent.receive(mt);
			if(msg!=null) {
				String newLightValue = msg.getContent();
					setCurrentLight(BathroomLights.valueOf(newLightValue));
			}else {
				block();
			}
		}

	}

	private class HandleCurrentAcutatorStateRequest extends CyclicBehaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
				reply.addReceiver(msg.getSender());
				reply.setContent(getCurrentLight().toString());
				myAgent.send(reply);
			} else {
				block();
			}
		}

	}

	public BathroomLights getCurrentLight() {
		return currentLight;
	}

	public void setCurrentLight(BathroomLights currentLight) {
		this.currentLight = currentLight;
	}

	@Override
	protected String getSHService() {
		return SHParameters.LIGHT_ACTUATOR;
	}

	@Override
	protected String generateRandomDeviceValues() {
		return null;
	}

}
