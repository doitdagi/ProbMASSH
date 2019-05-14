package it.sh.prob.mas.room.livingroom.devices;

import it.sh.prob.mas.SHAgent;
import it.sh.prob.mas.SHParameters;
import it.sh.prob.mas.room.livingroom.utilities.LivingroomLights;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class LivingroomLightController  extends SHAgent{

	private LivingroomLights currentLight;
	
	private final String CURRENT_LIGHT ="current_light";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void setup() {

		addBehaviour(new OneShotBehaviour() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void action() {
				registerDeviceController(SHParameters.LIVINGROOM_LIGHT_ACTUATOR);
			}
		});
		
		
		
		
		//Set new light value
		addBehaviour(new CyclicBehaviour() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void action() {
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
				ACLMessage msg = myAgent.blockingReceive(mt);
				String newLightValue = msg.getContent();
				setCurrentLight(LivingroomLights.valueOf(newLightValue));
			}
		});
	
		
		addBehaviour(new CyclicBehaviour() {
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
				}else {
					block();
				}
			}
		});

	}
	
	
	public LivingroomLights getCurrentLight() {
		return currentLight;
	}

	public void setCurrentLight(LivingroomLights currentLight) {
		this.currentLight = currentLight;
	}
}
