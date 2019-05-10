package it.sh.prob.mas.room.bathroom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.print.attribute.standard.Finishings;

import it.sh.prob.mas.SHAgent;
import it.sh.prob.mas.utilites.BathroomLights;
import it.sh.prob.mas.utilites.SHServices;
import it.sh.prob.mas.utilites.UserCommands;
import it.sh.prob.mas.utilities.problog.JProblog;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class BathReasonerAgent extends SHAgent {

	private static final String BATH_LIGHT_RULE_PATH =  "rules/light_rule.pl";
	JProblog jp = new JProblog();

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	
	@Override
	protected void setup() {
		addBehaviour(new ReasoningBehavior());
	}

	private class ReasoningBehavior extends CyclicBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			// TODO: I AM WORKING ONLY ON THE USER COMMAND NOW
			// wait for user command or change in environment parameters
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage userRequest = myAgent.receive(mt);

			if (userRequest != null) {
				UserCommands userCommand = UserCommands.valueOf(userRequest.getContent());
				switch (userCommand) {
				case TURN_ON_LIGHT:
					// TODO: FINISH EVERYTHING HERE
//					//look for all relevant services for turn on light 
					SHServices service = getService(userCommand);
					List<AID> serviceProviderAgents = getDeviceServiceProviders(service, myAgent);

					if (serviceProviderAgents.isEmpty()) {
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
						MessageTemplate infoMT = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
						List<ACLMessage> informationList = new ArrayList<ACLMessage>();
						int noOfInformationProvider = serviceProviderAgents.size();
						while (noOfInformationProvider > 0) {
							ACLMessage reply = myAgent.blockingReceive(infoMT);
							informationList.add(reply);
							noOfInformationProvider--;
 						}

						
						String problogModel =	buildProblogModel(service, informationList);
						
						

						//TODO: RUN PROBLOG USING THE FRAMEWORK
						double startT = System.currentTimeMillis();					
						String ss = jp.apply(problogModel);
						double finisihT = System.currentTimeMillis();
						double totalT = (finisihT-startT);
						System.out.println("total time,,,,,,,,(SEC) :" +totalT );

						System.out.println("=============");
						
						runProlog();
						
//
//						
						
						//	System.out.println(ss);
//						//wait for reply from every devices within the deadline 
//						//aggregiate the reply
//						//form the prolog model and reason
//						//send command 
					}
//					
//					
//					
//					//determine relevant device agents 
//					//send request 
//					//reason 
//					
//					
//					
					break;
////				case HEAT_UP_THE_ROOM:
////					System.out.println("heat up the room command");
////					break;
				default:
					System.out.println("Default, do nothing");
					break;
				}
			}

			// Determine the devices agents related with the event
			// and request for the relevant information

			// Determine the success probability

			// execute the command

		}

	}

	/**
	 * Given user command find the relevant service associated with it
	 * 
	 * @param userCMD USER COMMAND SHOULD BE DEFINED AS
	 *                OPERATION_ACTION_OBJECT/THING
	 * @return
	 */
	private SHServices getService(UserCommands userCMD) {
		String serviceName = userCMD.toString().split("_")[2];
		return SHServices.valueOf(serviceName);
	}

	
	private String buildProblogModel(SHServices service, List<ACLMessage> informationList) {
		String model = "";
		for (ACLMessage aclMessage : informationList) {
			model = model + aclMessage.getContent()+"\n";
		}		
		switch (service) {
		case LIGHT:
			List<String> list = new ArrayList<>();
			try (BufferedReader br = Files.newBufferedReader(Paths.get(BATH_LIGHT_RULE_PATH))) {
				list = br.lines().collect(Collectors.toList());
			} catch (IOException e) {
				e.printStackTrace();
			}
			for (String string : list) {
				model = model+ string +"\n"; 
			}
			model = model +	buildQuery(service);
			break;
		case  TEMPERATURE:
			break;
		default:
			break;
		}
		return model;
	}
	
	
	/**
	 * ruN PROLOG FROM COMMAND LINE
	 *TODO: BUILD FILE OR TRY TO RUN RUN THE STRING AS PIP
	 */
	private void runProlog() {
		//create file file temp
		//run problog on the file
		//take the result 
		//measure file
		try {
			double startT = System.currentTimeMillis();					
			Runtime rt = Runtime.getRuntime();
			Process pr = rt.exec("problog /home/fd/Documents/phd/workspace/ProbMASSH/rules/test.pl");
			BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String line=null;
			Stream<String> result = input.lines();
		 	double finisihT = System.currentTimeMillis();
			
			double totalT = (finisihT-startT);
			System.out.println("total time(SEC) :" +totalT );
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

	private String buildQuery(SHServices service) {
		String query = "";
		switch (service) {
		case LIGHT:
			for(BathroomLights bls : BathroomLights.values()) {
				query = query+ "query("+bls+").\n";
			}
  			break;
		default:
			break;
 		}

	return query; 
	}
	
	
}
