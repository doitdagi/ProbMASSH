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
import it.sh.prob.mas.utilites.UserCommands;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public abstract class SHReasonerAgent extends SHAgent {

	private static final String BATH_LIGHT_RULE_PATH = "rules/light_rule.pl";

	/**
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Reason about lighting of the room
	 * 
	 * @param myAgent     the reasoner agent
	 * @param usrCMD      The user command
	 * @param sensor      The kind of sensor it needs to monitor to perform the
	 *                    reasoning
	 * @param actuator    The actuator it needs to control
	 * @param local_DF_ID The DF that it needs to communicate with to collect the
	 *                    data from sensors and to control the parameters of the
	 *                    actuator
	 */
	protected void reasonAboutLight(Agent myAgent, UserCommands usrCMD, String sensor, String actuator,
			AID local_DF_ID) {
		List<String> dataFromLocalProviders = getSensorDataFromLocalProviders(myAgent, sensor, local_DF_ID);
		List<String> missingInformation = existsMissingData(dataFromLocalProviders, usrCMD);
		String proLogModel = "";
		String probLogResult;
		if (!missingInformation.isEmpty()) {
			List<String> missingDataFromGlobalProviders = getMissingDataFromGlobalProviders(missingInformation,
					myAgent);
			dataFromLocalProviders.addAll(missingDataFromGlobalProviders);
			missingInformation = existsMissingData(dataFromLocalProviders, usrCMD);
			if (!missingInformation.isEmpty()) {
				List<String> defaultValues = getDefaultLocalSensorData(missingInformation, myAgent, local_DF_ID);
				dataFromLocalProviders.addAll(defaultValues);
			}
		}
		proLogModel = buildProblogModel(getService(actuator), dataFromLocalProviders);
		probLogResult = getProbLogResult(proLogModel);
		sendActuationCommand(actuator, probLogResult, myAgent, local_DF_ID);
		System.out.println(probLogResult);
	}

	private List<String> getSensorDataFromLocalProviders(Agent myAgent, String sensor, AID local_DF_ID) {
		List<AID> sensorDataProviders = getSensorDataProviders(sensor, myAgent, local_DF_ID);
		List<String> informationList = new ArrayList<String>();
		ACLMessage requestInformation = new ACLMessage(ACLMessage.REQUEST);
		for (AID aid : sensorDataProviders) {
			requestInformation.addReceiver(aid);
			requestInformation.setContent("");
			myAgent.send(requestInformation);
		}
		// WAIT UNTIL ALL PROVIDERS REPIES
		MessageTemplate infoMT;
		for (AID aid : sensorDataProviders) {
			infoMT = MessageTemplate.MatchSender(aid);
			ACLMessage reply = myAgent.blockingReceive(infoMT);
			informationList.add(reply.getContent());
		}

		return informationList;
	}

	private List<String> existsMissingData(List<String> localCollectedData, UserCommands usrCMD) {
		List<String> missingData = new ArrayList<String>();
		List<String> essentialSensors = getEssentialSensors(usrCMD);
		for (String sensor : essentialSensors) {
			boolean sensorDataRecived = false;
			for (String sensorData : localCollectedData) {
				if (sensorData.contains(sensor)) {
					sensorDataRecived = true;
					break;
				}
			}
			if (!sensorDataRecived) {
				missingData.add(sensor);
			}
		}

		return missingData;
	}

	private List<String> getMissingDataFromGlobalProviders(List<String> missingInformation, Agent myAgent) {
		List<String> collectedData = new ArrayList<String>();
		for (String mi : missingInformation) {
			ACLMessage requestmInfo = new ACLMessage(ACLMessage.REQUEST);
			requestmInfo.addReceiver(toAID(getNegotiatorAgentID()));
			requestmInfo.setContent(mi);
			myAgent.send(requestmInfo);
		}

		// WAIT UNTIL THE NEGOTIATOR REPLIES FOR THE REQUEST
		MessageTemplate negoReplyTemplate = MessageTemplate.MatchSender(toAID(getNegotiatorAgentID()));
		for (int i = 0; i < missingInformation.size(); i++) {
			ACLMessage msg = myAgent.blockingReceive(negoReplyTemplate);
			if (msg.getPerformative() == ACLMessage.INFORM) {
				collectedData.add(msg.getContent());
			}
		}

		return collectedData;
	}

	private List<String> getDefaultLocalSensorData(List<String> missingInformation, Agent myAgent, AID local_DF_ID) {
		List<String> defaultSensorData = new ArrayList<String>();
		for (String mi : missingInformation) {
			System.out.println(mi);
			defaultSensorData.add(formulateDefaultLocalSensorData(mi));
			System.out.println("Default value for: " + mi + "...." + formulateDefaultLocalSensorData(mi));
		}

		return defaultSensorData;
	}

	protected String formulateDefaultLocalSensorData(String variable) {
		return generateRandomCertaintiyValues() + PREFIX + variable + "(" + getDefaultValue(variable) + POSTFIX;
	}

	private String getService(String actuator) {
		return actuator.toString().split("_")[0];
	}

	private List<String> getEssentialSensors(UserCommands usrCMD) {
		List<String> sensors = new ArrayList<String>();
		switch (usrCMD) {
		case TURN_ON_LIGHT:
			sensors.add(SHSensors.activity.toString());
			sensors.add(SHSensors.location.toString());
			sensors.add(SHSensors.luminosity.toString());
			break;

		default:
			break;
		}

		return sensors;
	}

	private void sendActuationCommand(String actuator, String command, Agent myAgent, AID local_DF_ID) {
		AID lightController = getDeviceControllerAgent(actuator, myAgent, local_DF_ID);
		ACLMessage cmd = new ACLMessage(ACLMessage.INFORM);
		cmd.addReceiver(lightController);
		cmd.setContent(command);
		myAgent.send(cmd);

	}

	protected String buildProblogModel(String service, List<String> informationList) {
		String model = "";
		for (String info : informationList) {
			model = model + info + "\n";
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

	protected String getProbLogResult(String probLogModel) {
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

	@Override
	protected List<String> getSHService() {
		// TODO Auto-generated method stub
		return null;
	}

	protected abstract String getDefaultValue(String sensorName);
}
