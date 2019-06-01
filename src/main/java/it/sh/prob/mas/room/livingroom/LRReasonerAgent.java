package it.sh.prob.mas.room.livingroom;

import java.nio.file.Path;
import java.nio.file.Paths;

import it.sh.prob.mas.SHParameters;
import it.sh.prob.mas.SHReasonerAgent;
import it.sh.prob.mas.room.bathroom.utilites.BathroomSensors;
import it.sh.prob.mas.room.livingroom.utilities.LivingroomInhabitantActivitities;
import it.sh.prob.mas.room.livingroom.utilities.LivingroomLights;
import it.sh.prob.mas.room.livingroom.utilities.LivingroomLocations;
import it.sh.prob.mas.room.livingroom.utilities.LivingroomLumValues;
import it.sh.prob.mas.room.livingroom.utilities.LivingroomTempValues;
import it.sh.prob.mas.utilites.AgentID;
import it.sh.prob.mas.utilites.SHACLProtocolID;
import it.sh.prob.mas.utilites.UserCommands;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class LRReasonerAgent extends SHReasonerAgent {

	private static final String LIVINROOM_LIGHT_RULE_PATH = "rules/livingroom_light_rule.pl";
	private static final String LIVINROOM_TEMP_RULE_PATH = "";

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
			ACLMessage msg_userrequest = myAgent.receive(mt_userrequest);
			ACLMessage msg_negotatiorrequest = myAgent.receive(mt_negotatior_request);
			if (msg_userrequest != null) {
				UserCommands userCommand = UserCommands.valueOf(msg_userrequest.getContent());
				switch (userCommand) {
				case TURN_ON_LIGHT:
					reasonAboutLight(myAgent, UserCommands.TURN_ON_LIGHT, SHParameters.LIGHT_SENSOR,
							SHParameters.LIGHT_ACTUATOR, toAID(AgentID.LIVINGROOM_DF_AID));
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
		return AgentID.LIVINGROOM_NEGOTIATOR_AID;
	}

	@Override
	protected String buildQuery(String service) {
		String query = "";
		switch (service) {
		case SHParameters.LIGHT:
			for (LivingroomLights bls : LivingroomLights.values()) {
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
			result = LivingroomInhabitantActivitities.normal_daily_activites.toString();
			break;
		case location:
			result = LivingroomLocations.zoneone.toString();
			break;
		case luminosity:
			result = LivingroomLumValues.bright.toString();
			break;
		case temperature:
			result = LivingroomTempValues.hot.toString();
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
			return Paths.get(LIVINROOM_LIGHT_RULE_PATH);
		case SHParameters.TEMPERATURE:
			return Paths.get(LIVINROOM_TEMP_RULE_PATH);
		}
		return null;
	}
}
