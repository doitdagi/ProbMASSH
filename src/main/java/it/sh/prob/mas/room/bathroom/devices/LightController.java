package it.sh.prob.mas.room.bathroom.devices;

import it.sh.prob.mas.SHAgent;
import it.sh.prob.mas.SHParameters;
import it.sh.prob.mas.utilites.BathroomLights;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class LightController extends SHAgent{

	private BathroomLights currentLight;
	
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
				registerDeviceController(SHParameters.BATHROOM_LIGHT_ACTUATOR);
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
				setCurrentLight(BathroomLights.valueOf(newLightValue));
				System.out.println("New light value set to :"+ newLightValue);
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
	
	
	public BathroomLights getCurrentLight() {
		return currentLight;
	}


	public void setCurrentLight(BathroomLights currentLight) {
		this.currentLight = currentLight;
	}
	
}
