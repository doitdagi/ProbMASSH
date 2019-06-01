package it.sh.prob.mas.room.bedroom;

import java.util.HashMap;
import java.util.Map;

import it.sh.prob.mas.MASStarter;
import it.sh.prob.mas.SHParameters;
import it.sh.prob.mas.utilites.AgentID;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.StaleProxyException;

public class BedroomMASStarter extends MASStarter{
	protected static Map<String, String> memberAgents = new HashMap<String, String>();

	/**
	 * 
	 * @param args IS an array of size four args[0] boolean if the container is main
	 *             or not args[1] boolean if we need to display GUI or not args[2]
	 *             String: the host IP address args[3] String: the host port address
	 */
	public static void main(String[] args) {
		if (args.length != 5) {
			System.err.println("INVALID PARAMETER SIZE");
			return;
		}
		if (args[0].equals(MAIN)) {
			isMain = true;
		}else {
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
				if (((agent.getKey() == AgentID.BEDROOM_NEGOTIATOR_AID)||(agent.getKey() == AgentID.BEDROOM_REASONER_AID)) && ableToReason) {
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
		memberAgents.put(AgentID.BEDROOM_DF_AID, "it.sh.prob.mas.room.bedroom.BedroomDF");
		memberAgents.put(AgentID.BEDROOM_NEGOTIATOR_AID, "it.sh.prob.mas.room.bedroom.BedNegotiatorAgent");
		memberAgents.put(AgentID.BEDROOM_NEGOTIATOR_AID, "it.sh.prob.mas.room.bedroom.BedNegotiatorAgent");
		memberAgents.put(AgentID.BEDROOM_REASONER_AID, "it.sh.prob.mas.room.bedroom.BedReasonerAgent");
	 
		memberAgents.put(AgentID.BEDROOM_LUMINIOSITY_SENSOR_AID, "it.sh.prob.mas.room.bedroom.devices.BedroomLuminositySensor");
		memberAgents.put(AgentID.BEDROOM_ACTIVITIY_RECOGNITION_AID, "it.sh.prob.mas.room.bedroom.devices.BedroomActivityRecognition");
		memberAgents.put(AgentID.BEDROOM_INHABITANT_LOCALIZATION_SENSOR_AID, "it.sh.prob.mas.room.bedroom.devices.BedroomInhabitantLocalization");
		memberAgents.put(AgentID.BEDROOM_TEMPERATURE_SENSOR_AID,
				"it.sh.prob.mas.room.bedroom.devices.BedroomTempSensor");
		
		memberAgents.put(AgentID.BEDROOM_LIGHT_CONTROLLER_AID, "it.sh.prob.mas.room.bedroom.devices.BedroomLightController");

	
	}
	
}
