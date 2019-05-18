package it.sh.prob.mas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import it.sh.prob.mas.room.bathroom.utilites.BathroomLights;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class SHReasonerAgent extends SHAgent {

	private static final String BATH_LIGHT_RULE_PATH = "rules/light_rule.pl";

	/**
	 */
	private static final long serialVersionUID = 1L;

 
	/**
	 * Reason about lighting of the room
	 * 
	 * @param myAgent     the reasoner agent
	 * @param sensor      The kind of sensor it needs to monitor to perform the
	 *                    reasoning
	 * @param actuator    The actuator it needs to control
	 * @param local_DF_ID The DF that it needs to communicate with to collect the
	 *                    data from sensors and to control the parameters of the
	 *                    actuator
	 */
	protected void reasonAboutLight(Agent myAgent, String sensor, String actuator, AID local_DF_ID) {
		List<AID> sensorDataProviders;
		sensorDataProviders = getSensorDataProviders(sensor, myAgent, local_DF_ID);
		if (sensorDataProviders.isEmpty()) {
			String problogModel = buildProblogModel(SHParameters.LIGHT, null);
			String selectedCMD = runProlog(problogModel);
			// Send command for the actuator device
			AID lightController = getDeviceControllerAgent(actuator, myAgent, local_DF_ID);
			ACLMessage cmd = new ACLMessage(ACLMessage.INFORM);
			cmd.addReceiver(lightController);
			cmd.setContent(selectedCMD);
			myAgent.send(cmd);
		} else {
			// send request for service provider agents
			System.out.println("Not empty :"+ sensorDataProviders.size());
			ACLMessage requestInformation = new ACLMessage(ACLMessage.REQUEST);
			for (AID aid : sensorDataProviders) {
				requestInformation.addReceiver(aid);
				requestInformation.setContent("");
				myAgent.send(requestInformation);

			}
			// WAIT UNTIL ALL PROVIDERS REPIES
			List<ACLMessage> informationList = new ArrayList<ACLMessage>();
			MessageTemplate infoMT;
			for (AID aid : sensorDataProviders) {
				infoMT = MessageTemplate.MatchSender(aid);
				ACLMessage reply = myAgent.blockingReceive(infoMT);
				informationList.add(reply);
			}

			// BUILD THE PROBLOG MODEL
			String problogModel = buildProblogModel(SHParameters.LIGHT, informationList);
			String selectedCMD = runProlog(problogModel);

			// Send command for the actuator device
			AID lightController = getDeviceControllerAgent(actuator, myAgent, local_DF_ID);
			ACLMessage cmd = new ACLMessage(ACLMessage.INFORM);
			cmd.addReceiver(lightController);
			cmd.setContent(selectedCMD);
			System.out.println("KONJO:" + selectedCMD);
			myAgent.send(cmd);
		}

	}

	protected String buildProblogModel(String service, List<ACLMessage> informationList) {
		String model = "";
		System.out.println("INFORMATION LIST SIE"+informationList==null);
		for (ACLMessage aclMessage : informationList) {
			model = model + aclMessage.getContent() + "\n";
		}
		switch (service) {
		case SHParameters.LIGHT:
			List<String> list = new ArrayList<>();
			try (BufferedReader br = Files.newBufferedReader(Paths.get(BATH_LIGHT_RULE_PATH))) {
				list = br.lines().collect(Collectors.toList());
			} catch (IOException e) {
				e.printStackTrace();
			}
			for (String string : list) {
				model = model + string + "\n";
			}
			model = model + buildQuery(service);
			break;
		case SHParameters.TEMPERATURE:
			break;
		default:
			break;
		}
		return model;
	}

	protected String runProlog(String probLogModel) {
		String command = "";
		double value = 0;
		double newValue = 0;
		String[] line; // a line from the result
		String model = "printf \"" + probLogModel + "\"";
		String[] cmd = { "/bin/sh", "-c", model + "| problog" };

		try {
			// Run problog
			Runtime rt = Runtime.getRuntime();
			Process pr = rt.exec(cmd);
			pr.waitFor();
			BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			Object[] result = input.lines().toArray();
			// select the the command with max probability
			for (int i = 0; i < result.length; i++) {
				line = ((String) result[i]).split(":");
				newValue = Double.valueOf(line[1].trim());
				if (newValue >= value) {
					value = newValue;
					command = line[0].trim();
				}
			}
			return command;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String buildQuery(String service) {
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

}
