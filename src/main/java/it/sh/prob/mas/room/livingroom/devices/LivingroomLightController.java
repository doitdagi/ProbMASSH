package it.sh.prob.mas.room.livingroom.devices;

import java.util.ArrayList;
import java.util.List;

import it.sh.prob.mas.SHDeviceAgent;
import it.sh.prob.mas.SHParameters;
import it.sh.prob.mas.room.livingroom.utilities.LivingroomLights;
import it.sh.prob.mas.utilites.AgentID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class LivingroomLightController  extends SHDeviceAgent{

	private LivingroomLights currentLight;
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static List<String> services = new ArrayList<String>();
	
	static {
		services.add(SHParameters.LIGHT_ACTUATOR);
	}
	@Override
	protected void setup() {
		addBehaviour(new RegisterSHServices(toAID(AgentID.LIVINGROOM_DF_AID)));
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
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM), MessageTemplate.MatchSender(toAID(AgentID.LIVINGROOM_REASONER_AID)));
			ACLMessage msg = myAgent.receive(mt);
			if (msg!=null) {
				String newLightValue = msg.getContent();
				setCurrentLight(LivingroomLights.valueOf(newLightValue));
			} else {
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

	public LivingroomLights getCurrentLight() {
		return currentLight;
	}

	public void setCurrentLight(LivingroomLights currentLight) {
		this.currentLight = currentLight;
	}

	@Override
	protected List<String> getSHService() {
		return services;
	}

	@Override
	protected String generateRandomDeviceValues() {
		return null;
	}
}
