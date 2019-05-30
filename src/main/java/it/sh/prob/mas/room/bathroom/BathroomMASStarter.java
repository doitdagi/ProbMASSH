package it.sh.prob.mas.room.bathroom;

import java.util.HashMap;
import java.util.Map;

import it.sh.prob.mas.MASStarter;
import it.sh.prob.mas.SHParameters;
import it.sh.prob.mas.utilites.AgentID;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.StaleProxyException;

public class BathroomMASStarter extends MASStarter {

	protected static Map<String, String> memberAgents = new HashMap<String, String>();

	/**
	 * 
	 * @param args IS an array of size four args[0] if the container is main args[1]
	 *             boolean if we need to display GUI args[2] String: the host IP
	 *             address args[3] String: the host port address
	 */
	public static void main(String[] args) {
		if (args.length != 5) {
			System.err.println("INVALID PARAMETER SIZE");
			return;
		}
		if (args[0].equals(MAIN)) {
			isMain = true;
		} else {
			if (validIP(args[2])) {
				HOST_ADDRESS = args[2];
			} else {
				System.err.println("INVALID HOST ADDRESS");
				return;
			}

			if (validPort(args[3])) {
				HOST_PORT = args[3];
			} else {
				System.err.println("INVALID HOST PORT");
				return;
			}
		}
		if (args[1].equals(GUI)) {
			displayGUI = true;
		}
		if (args[4].contentEquals(REASONING)) {
			ableToReason = true;
		}

		initializeMemberAgents();
		Profile p = new ProfileImpl();
		AgentContainer ac;
		if (displayGUI) {
			p.setParameter(Profile.GUI, Boolean.TRUE.toString());
		}

		if (isMain) {
			ac = jade.core.Runtime.instance().createMainContainer(p);
			memberAgents.put(AgentID.HOUSE_DF_AID, "it.sh.prob.mas.HouseLevelDF");
		} else {
			p.setParameter(Profile.MAIN_HOST, HOST_ADDRESS);
			p.setParameter(Profile.MAIN_PORT, HOST_PORT);
			ac = jade.core.Runtime.instance().createAgentContainer(p);
		}

		// add and start all agent
		for (Map.Entry<String, String> agent : memberAgents.entrySet()) {
			try {
				if (((agent.getKey() == AgentID.KITCHEN_NEGOTIATOR_AID)||(agent.getKey() == AgentID.BATHROOM_REASONER_AID)) && ableToReason) {
					ac.createNewAgent(agent.getKey(), agent.getValue(), new String[] { SHParameters.REASONING })
							.start();
				} else {
					ac.createNewAgent(agent.getKey(), agent.getValue(), null).start();
				}
			} catch (StaleProxyException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Sucessfully started...");
	}

	private static void initializeMemberAgents() {
		memberAgents.put(AgentID.BATHROOM_DF_AID, "it.sh.prob.mas.room.bathroom.BathroomDF");
		memberAgents.put(AgentID.BATHROOM_NEGOTIATOR_AID, "it.sh.prob.mas.room.bathroom.BathNegotiatorAgent");
		memberAgents.put(AgentID.BATHROOM_REASONER_AID, "it.sh.prob.mas.room.bathroom.BathReasonerAgent");

		memberAgents.put(AgentID.BATHROOM_LUMINIOSITY_SENSOR_AID,
				"it.sh.prob.mas.room.bathroom.devices.BathLuminositySensor");
		memberAgents.put(AgentID.BATHROOM_ACTIVITIY_RECOGNITION_AID,
				"it.sh.prob.mas.room.bathroom.devices.BathActivityRecognition");
		memberAgents.put(AgentID.BATHROOM_INHABITANT_LOCALIZATION_SENSOR_AID,
				"it.sh.prob.mas.room.bathroom.devices.BathInhabitantLocalization");
		memberAgents.put(AgentID.BATHROOM_TEMPERATURE_SENSOR_AID,
				"it.sh.prob.mas.room.bathroom.devices.BathroomTempSensor");

		//
		memberAgents.put(AgentID.BATHROOM_LIGHT_CONTROLLER_AID,
				"it.sh.prob.mas.room.bathroom.devices.BathLightController");
	}

}
