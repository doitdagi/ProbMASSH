package it.sh.prob.mas.room.bathroom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import it.sh.prob.mas.SHAgent;
import it.sh.prob.mas.SHParameters;
import it.sh.prob.mas.utilites.BathroomLights;
import it.sh.prob.mas.utilites.SHServices;
import it.sh.prob.mas.utilites.UserCommands;
import it.sh.prob.mas.utilities.problog.JProblog;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class BathReasonerAgent extends SHAgent {

	private static final String BATH_LIGHT_RULE_PATH = "rules/light_rule.pl";
	JProblog jp = new JProblog();

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void setup() {
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
			List<AID> serviceProviderAgents;
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage userRequest = myAgent.receive(mt);

			if (userRequest != null) {
				UserCommands userCommand = UserCommands.valueOf(userRequest.getContent());
				switch (userCommand) {
				case TURN_ON_LIGHT:
//					//look for all relevant services for turn on light 
					serviceProviderAgents = getDeviceServiceProviders(SHParameters.BATHROOM_LIGHT_SENSOR, myAgent);
					
					if (serviceProviderAgents.isEmpty()) {
						// IF NO SERVICE PROVIDER OR DEVICE AGENTS FOR THE REQUIRED USER COMMAND BASED
						// ON DEFAULT VALUES OF THE RULE
						// Reason by default values
						// or send failure messages
					} else {
						// send request for service provider agents
						ACLMessage requestInformation = new ACLMessage(ACLMessage.REQUEST);
						for (AID aid : serviceProviderAgents) {
							requestInformation.addReceiver(aid);
							requestInformation.setContent("");
							myAgent.send(requestInformation);
						
						}
						// WAIT UNTIL ALL PROVIDERS REPIES
						List<ACLMessage> informationList = new ArrayList<ACLMessage>();
						MessageTemplate infoMT;
						for (AID aid : serviceProviderAgents) {
							 infoMT = MessageTemplate.MatchSender(aid);
								ACLMessage reply = myAgent.blockingReceive(infoMT);
								informationList.add(reply);
						}
 
						// BUILD THE PROBLOG MODEL
						String problogModel = buildProblogModel(SHParameters.LIGHT, informationList);

						// TODO: RUN PROBLOG USING THE FRAMEWORK
 //						String ss = jp.apply(problogModel);
  					//	String selectedCMD = runProlog();
  						String selectedCMD = runProlog(problogModel);
  						 
  						// Send command for the actuator device
						 AID lightController = 	getDeviceControllerAgent(SHParameters.BATHROOM_LIGHT_ACTUATOR, myAgent); 
						ACLMessage cmd = new ACLMessage(ACLMessage.INFORM);
						cmd.addReceiver(lightController);
						cmd.setContent(selectedCMD);
						myAgent.send(cmd);
 					}
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

	/**
	 * Given user command find the relevant service/ DATA Providers which are
	 * required to evaluate and execute the user command associated with it
	 * 
	 * @param userCMD USER COMMAND SHOULD BE DEFINED AS
	 *                OPERATION_ACTION_OBJECT/THING
	 * @return
	 */
	private SHServices getService(UserCommands userCMD) {
		String serviceName = userCMD.toString().split("_")[2];
		return SHServices.valueOf(serviceName);
	}

	private String buildProblogModel(String service, List<ACLMessage> informationList) {
		String model = "";
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
		String[] line; //a line from the result
		String model = "printf \""+probLogModel+"\"";
		String[] cmd = {
				"/bin/sh",
				"-c",
				model + "| problog"
				};
		
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
