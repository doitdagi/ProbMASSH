package it.sh.prob.mas.room.bedroom;

import java.nio.file.Path;
import java.nio.file.Paths;

import it.sh.prob.mas.SHParameters;
import it.sh.prob.mas.SHReasonerAgent;
import it.sh.prob.mas.room.bedroom.utilites.BedroomInhabitantActivitities;
import it.sh.prob.mas.room.bedroom.utilites.BedroomLights;
import it.sh.prob.mas.room.bedroom.utilites.BedroomLocations;
import it.sh.prob.mas.room.bedroom.utilites.BedroomLumValues;
import it.sh.prob.mas.room.bedroom.utilites.BedroomSensors;
import it.sh.prob.mas.room.bedroom.utilites.BedroomTempValues;
import it.sh.prob.mas.utilites.AgentID;
import it.sh.prob.mas.utilites.SHACLProtocolID;
import it.sh.prob.mas.utilites.UserCommands;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class BedReasonerAgent extends SHReasonerAgent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String BEDROOM_LIGHT_RULE_PATH = "rules/bedroom_light_rule.pl";
	private static final String BEDROOM_TEMP_RULE_PATH = "";

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
			ACLMessage msg_userrequest = myAgent.receive(mt_userrequest);
			ACLMessage msg_negotatiorrequest = myAgent.receive(mt_negotatior_request);
			if (msg_userrequest != null) {
				UserCommands userCommand = UserCommands.valueOf(msg_userrequest.getContent());
				switch (userCommand) {
				case TURN_ON_LIGHT:
					reasonAboutLight(myAgent, UserCommands.TURN_ON_LIGHT, SHParameters.LIGHT_SENSOR,
							SHParameters.LIGHT_ACTUATOR, toAID(AgentID.BEDROOM_DF_AID));
					break;
				case HEAT_UP_ROOM:
					System.out.println("heat up the room command");
					break;
				default:
					System.out.println("Default, do nothing");
					break;
				}
			}else if (msg_negotatiorrequest != null) {
				String prologResult = getProbLogResult(msg_negotatiorrequest.getContent());
				ACLMessage reply_to_negotiator = msg_negotatiorrequest.createReply();
				reply_to_negotiator.setContent(prologResult);
				reply_to_negotiator.setProtocol(SHACLProtocolID.REASONING_RESULT_FROM_REASONER_TO_NEGOTIATOR);
				reply_to_negotiator.setPerformative(ACLMessage.INFORM);
				myAgent.send(reply_to_negotiator);
			} else {
				block();
			}
		}

	}

	@Override
	protected String getNegotiatorAgentID() {
		return AgentID.BEDROOM_NEGOTIATOR_AID;
	}

	@Override
	protected String buildQuery(String service) {
		String query = "";
		switch (service) {
		case SHParameters.LIGHT:
			for (BedroomLights bls : BedroomLights.values()) {
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
		BedroomSensors sensor = BedroomSensors.valueOf(sensorName);
		String result = "";
		switch (sensor) {
		case activity:
			result = BedroomInhabitantActivitities.sleeping.toString();
			break;
		case location:
			result = BedroomLocations.zoneone.toString();
			break;
		case luminosity:
			result = BedroomLumValues.bright.toString();
			break;
		case temperature:
			result = BedroomTempValues.hot.toString();
			break;
		default:
			break;
		}
		return result;
	}

	@Override
	protected Path getRulePath(String service) {
		switch (service) {
		case SHParameters.LIGHT:
			return Paths.get(BEDROOM_LIGHT_RULE_PATH);
		case SHParameters.TEMPERATURE:
			return Paths.get(BEDROOM_TEMP_RULE_PATH);
		}
		return null;
	}
}
