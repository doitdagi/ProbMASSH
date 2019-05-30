package it.sh.prob.mas.room.bathroom;

import java.util.ArrayList;
import java.util.List;

import it.sh.prob.mas.SHParameters;
import it.sh.prob.mas.SHReasonerAgent;
import it.sh.prob.mas.room.bathroom.utilites.BathroomInhabitantActivitityValues;
import it.sh.prob.mas.room.bathroom.utilites.BathroomLights;
import it.sh.prob.mas.room.bathroom.utilites.BathroomLocations;
import it.sh.prob.mas.room.bathroom.utilites.BathroomLumValues;
import it.sh.prob.mas.room.bathroom.utilites.BathroomSensors;
import it.sh.prob.mas.room.bathroom.utilites.BathroomTempValues;
import it.sh.prob.mas.utilites.AgentID;
import it.sh.prob.mas.utilites.UserCommands;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class BathReasonerAgent extends SHReasonerAgent {

	private static List<String> services = new ArrayList<String>();


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
							SHParameters.LIGHT_ACTUATOR, toAID(AgentID.BATHROOM_DF_AID));
					break;
//				case HEAT_UP_THE_ROOM:
//					System.out.println("heat up the room command");
//					break;
				default:
					System.out.println("Default, do nothing");
					break;
				}
			}

		}

	}

	@Override
	protected String getNegotiatorAgentID() {
		return AgentID.BATHROOM_NEGOTIATOR_AID;
	}
	
	
	@Override
	protected  String buildQuery(String service) {
		String query = "";
		switch (service) {
		case SHParameters.LIGHT:
			for (BathroomLights bls : BathroomLights.values()) {
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
			result = BathroomInhabitantActivitityValues.bathing.toString();
			break;
		case location:
			result = BathroomLocations.zoneone.toString();
			break;
		case luminosity:
			result = BathroomLumValues.bright.toString();
			break;
		case temperature:
			result = BathroomTempValues.hot.toString();
			break;
		default:
			break;
		}
		return result;
	}

	@Override
	protected List<String> getSHService() {
		return services;
	}
	
}
