package it.sh.prob.mas.room.bedroom;

import it.sh.prob.mas.SHParameters;
import it.sh.prob.mas.SHReasonerAgent;
import it.sh.prob.mas.utilites.AgentID;
import it.sh.prob.mas.utilites.UserCommands;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class BedReasonerAgent extends SHReasonerAgent{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void setup() {
		addBehaviour(new ReasoningBehavior());
	}
	
	// TODO: I AM WORKING ONLY ON THE USER COMMAND NOW
	// wait for user command or change in environment parametersBedroomSensorsBedroomSensors
	private class ReasoningBehavior extends CyclicBehaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage userRequest = myAgent.receive(mt);
			if (userRequest != null) {
				UserCommands userCommand = UserCommands.valueOf(userRequest.getContent());
				switch (userCommand) {
				case TURN_ON_LIGHT:
					reasonAboutLight(myAgent,UserCommands.TURN_ON_LIGHT, SHParameters.LIGHT_SENSOR,SHParameters.LIGHT_ACTUATOR, toAID(AgentID.BEDROOM_DF_AID));
					break;
////				case HEAT_UP_THE_ROOM:
////					System.out.println("heat up the room command");
////					break;
				default:
					System.out.println("Default, do nothing");
					break;
				}
			}
		}

	}
	
	@Override
	protected String getNegotiatorAgentID() {
		return AgentID.BEDROOM_NEGOTIATOR_AID;
	}
}
