package it.sh.prob.mas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import it.sh.prob.mas.utilites.SHACLProtocolID;
import it.sh.prob.mas.utilites.UserCommands;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public abstract class SHReasonerAgent extends SHAgent {

	/**
	 */
	private static final long serialVersionUID = 1L;

	protected boolean hasProbLog = false;

	protected MessageTemplate mt_userrequest = MessageTemplate.and(
			MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
			MessageTemplate.MatchProtocol(SHACLProtocolID.USER_REQUEST));
	protected MessageTemplate mt_negotatior_request = MessageTemplate.and(
			MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
			MessageTemplate.MatchProtocol(SHACLProtocolID.REASONING_REQUEST_FROM_NEGOTIATOR_TO_REASONER));

	protected MessageTemplate mt_recive_reasoning_result = MessageTemplate.and(
			MessageTemplate.MatchPerformative(ACLMessage.INFORM),
			MessageTemplate.MatchSender(toAID(getNegotiatorAgentID())));

	protected abstract Path getRulePath(String service);

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

		// TODO
		if (hasProbLog) {
			probLogResult = getProbLogResult(proLogModel);
		} else {
			ACLMessage msg_request_negotiator = new ACLMessage(ACLMessage.REQUEST);
			msg_request_negotiator.addReceiver(toAID(getNegotiatorAgentID()));
			msg_request_negotiator.setProtocol(SHACLProtocolID.REASONING_REQUEST_FROM_REASONER_TO_NEGOTIATOR);
			msg_request_negotiator.setContent(proLogModel);
			myAgent.send(msg_request_negotiator);
			// WAIT UNTIL THE NEGOTIATOR REPLIES FOR THE REQUEST
			ACLMessage msg_recive_reasoning_result = myAgent.blockingReceive(mt_recive_reasoning_result);
			probLogResult = msg_recive_reasoning_result.getContent();
		}
		sendActuationCommand(actuator, probLogResult, myAgent, local_DF_ID);
		System.out.println(probLogResult + "..." + getLocalName());

	}

	/**
	 * 
	 * @param myAgent
	 * @param sensor
	 * @param local_DF_ID
	 * @return
	 */
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

	/**
	 * 
	 * @param localCollectedData
	 * @param usrCMD
	 * @return
	 */
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

	/**
	 * 
	 * @param missingInformation
	 * @param myAgent
	 * @return
	 */
	private List<String> getMissingDataFromGlobalProviders(List<String> missingInformation, Agent myAgent) {
		List<String> collectedData = new ArrayList<String>();
		MessageTemplate negoReplyTemplate = MessageTemplate.and(
				MessageTemplate.MatchSender(toAID(getNegotiatorAgentID())),
				MessageTemplate.or(
						MessageTemplate.MatchProtocol(SHACLProtocolID.MISSINGDATA_INFORM_NEGOTIATOR_TO_REASONER),
						MessageTemplate.MatchProtocol(SHACLProtocolID.MISSINGDATA_FAILURE_NEGOTIATOR_TO_REASOSNER)));

		for (String mi : missingInformation) {
			ACLMessage requestmInfo = new ACLMessage(ACLMessage.REQUEST);
			requestmInfo.addReceiver(toAID(getNegotiatorAgentID()));
			requestmInfo.setProtocol(SHACLProtocolID.MISSINGDATA_REQUEST_FROM_REASONER_TO_NEGOTIATOR);
			requestmInfo.setContent(mi);
			myAgent.send(requestmInfo);
			// WAIT UNTIL THE NEGOTIATOR REPLIES FOR THE REQUEST
			ACLMessage msg = myAgent.blockingReceive(negoReplyTemplate);
		// Add only if it the msg is information, recive but ignore the reject ones
			if (msg.getProtocol().contentEquals(SHACLProtocolID.MISSINGDATA_INFORM_NEGOTIATOR_TO_REASONER)) {
				collectedData.add(msg.getContent());				
			}
		}

		return collectedData;
	}

	/**
	 * 
	 * @param missingInformation
	 * @param myAgent
	 * @param local_DF_ID
	 * @return
	 */
	private List<String> getDefaultLocalSensorData(List<String> missingInformation, Agent myAgent, AID local_DF_ID) {
		List<String> defaultSensorData = new ArrayList<String>();
		for (String mi : missingInformation) {
			defaultSensorData.add(formulateDefaultLocalSensorData(mi));
		}

		return defaultSensorData;
	}

	/**
	 * 
	 * @param variable
	 * @return
	 */
	protected String formulateDefaultLocalSensorData(String variable) {
		return generateRandomCertaintiyValues() + PREFIX + variable + "(" + getDefaultValue(variable) + POSTFIX;
	}

	/**
	 * 
	 * @param actuator
	 * @return
	 */
	private String getService(String actuator) {
		return actuator.toString().split("_")[0];
	}

	/**
	 * 
	 * @param usrCMD
	 * @return
	 */
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

	/**
	 * 
	 * @param actuator
	 * @param command
	 * @param myAgent
	 * @param local_DF_ID
	 */
	private void sendActuationCommand(String actuator, String command, Agent myAgent, AID local_DF_ID) {
		AID lightController = getDeviceControllerAgent(actuator, myAgent, local_DF_ID);
		ACLMessage cmd = new ACLMessage(ACLMessage.INFORM);
		cmd.addReceiver(lightController);
		cmd.setContent(command);
		myAgent.send(cmd);

	}

	/**
	 * Build problog model
	 * 
	 * @param service
	 * @param informationList
	 * @return
	 */
	protected String buildProblogModel(String service, List<String> informationList) {
		String model = "";
		for (String info : informationList) {
			model = model + info + "\n";
		}
		switch (service) {
		case SHParameters.LIGHT:
			List<String> list = new ArrayList<>();
			try (BufferedReader br = Files.newBufferedReader(getRulePath(SHParameters.LIGHT))) {
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

	/**
	 * Run problog and extract the result The result is the query with max
	 * probablisitc value
	 * 
	 * @param probLogModel
	 * @return
	 */
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

	/**
	 * 
	 * @param service
	 * @return
	 */
	protected abstract String buildQuery(String service);

	/**
	 * Check if this reasoner has a problog engine.. TODO: WE CAN ALSO CHECK FROM
	 * THE SYSTEM
	 * 
	 * @return
	 */
	protected boolean hasProbLog() {
		if (getArguments() != null) {
			String reason = (String) getArguments()[0];
			if (reason.equals(SHParameters.REASONING)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected List<String> getSHService() {
		// TODO Auto-generated method stub
		return null;
	}
	protected abstract String getDefaultValue(String sensorName);
}
