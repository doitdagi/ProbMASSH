package it.sh.prob.mas.room.kitchen;

import it.sh.prob.mas.SHParameters;
import it.sh.prob.mas.SHReasonerAgent;
import it.sh.prob.mas.room.bathroom.utilites.BathroomSensors;
import it.sh.prob.mas.room.kitchen.utilities.KitchenInhabitantActivitities;
import it.sh.prob.mas.room.kitchen.utilities.KitchenLights;
import it.sh.prob.mas.room.kitchen.utilities.KitchenLocations;
import it.sh.prob.mas.room.kitchen.utilities.KitchenLumValues;
import it.sh.prob.mas.room.kitchen.utilities.KitchenTempValues;
import it.sh.prob.mas.utilites.AgentID;
import it.sh.prob.mas.utilites.UserCommands;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class KitReasonerAgent extends SHReasonerAgent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void setup() {
		hasProbLog = hasProbLog();
		addBehaviour(new ReasoningBehavior());
	}

	// TODO: I AM WORKING ONLY ON THE USER COMMAND NOW
	// wait for user command or change in environment parameters
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
					reasonAboutLight(myAgent, UserCommands.TURN_ON_LIGHT, SHParameters.LIGHT_SENSOR,
							SHParameters.LIGHT_ACTUATOR, toAID(AgentID.KITCHEN_DF_AID));
					break;
////					case HEAT_UP_THE_ROOM:
////						System.out.println("heat up the room command");
////						break;
				default:
					System.out.println("Default, do nothing");
					break;
				}
			}

		}

	}

	@Override
	protected String getNegotiatorAgentID() {
		return AgentID.KITCHEN_NEGOTIATOR_AID;
	}
	
	@Override
	protected  String buildQuery(String service) {
		String query = "";
		switch (service) {
		case SHParameters.LIGHT:
			for (KitchenLights bls : KitchenLights.values()) {
				query = query + "query(" + bls + ").\n";
			}
			break;
		default:
			break;
		}
		return query;
	}


	@Override
	protected String getDefaultValue(String sensorName) {
		BathroomSensors sensor = BathroomSensors.valueOf(sensorName);
		String result = "";
		switch (sensor) {
		case activity:
			result = KitchenInhabitantActivitities.cooking.toString();
			break;
		case location:
			result = KitchenLocations.cooking_area.toString();
			break;
		case luminosity:
			result = KitchenLumValues.bright.toString();
			break;
		case temperature:
			result = KitchenTempValues.hot.toString();
			break;
		default:
			break;
		}
		return result;
	}
}
