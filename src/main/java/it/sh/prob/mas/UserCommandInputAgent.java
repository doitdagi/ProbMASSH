package it.sh.prob.mas;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * This agent will act as command input for the MAS system
 * 
 * @author fd
 *
 */
public class UserCommandInputAgent extends Agent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void setup() {
	
		/**
		 * For the time begin it sends command only for the GUI
		 * But we may include commands from here for testing purposes
		 */
		addBehaviour(new ACKBehaviour());
	
	}
	
	
	private class ACKBehaviour extends CyclicBehaviour{

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage msg = myAgent.receive();
			if(msg != null) {
				System.out.println("ACK RECEVIED:..."+msg.getContent());
			}else {
				block();
			}
		}
	}
}
