package it.sh.prob.mas;

import java.util.ArrayList;
import java.util.List;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
  
public abstract class SHNegotiatorAgent extends SHAgent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	protected abstract AID getMyReasonerAID();

	protected abstract ISHSensors[] getSupportedServices();

	
	/**
	 * Register the services that this agent can provide
	 * 
	 * @author fd
	 *
	 */
	protected class RegisterGlobalServices extends OneShotBehaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public RegisterGlobalServices() {
		}

		@Override
		public void action() {
			registerGlobalServices(getAID(), getSupportedServices());
		}
	}
	
	
	
	/**
	 * This behavior is for requesting information from other Negotiation agents in
	 * other room
	 * 
	 * @author fd
	 *
	 */
	protected class InitiatorBehaviour extends CyclicBehaviour {
		/**
		 */
		//TODO: CHECK IF THE VARIABLES KEEP THEIR VALUES FOR FUTURE INTERACTION ALSO, THEY SHOULD NOT
		private static final long serialVersionUID = 1L;
		private SHSensors requestedData;
		private List<AID> serviceProviders;
		private String processedResult;
		int numberOfReplies;
		private List<ACLMessage> replyList;
		private ACLMessage response;
		private MessageTemplate mt_rq = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
		private MessageTemplate mt_inform = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
		private MessageTemplate mt_refuse = MessageTemplate.MatchPerformative(ACLMessage.REFUSE);

		public InitiatorBehaviour() {
		}

		@Override
		public void action() {
			ACLMessage msg_rq = myAgent.receive(mt_rq);
			ACLMessage msg_inform = myAgent.receive(mt_inform);
			ACLMessage msg_refuse = myAgent.receive(mt_refuse);
			if (msg_rq != null) {
				requestedData = SHSensors.valueOf(msg_rq.getContent());
				numberOfReplies = 0; // reset no of replies for each request
				replyList = new ArrayList<ACLMessage>();
				serviceProviders = getGlobalServiceProviders(requestedData, myAgent);
				if (serviceProviders.isEmpty()) {
					response = new ACLMessage(ACLMessage.FAILURE);
					response.addReceiver(getMyReasonerAID());
					myAgent.send(response);
				} else {
					//TODO: CHECK IF IT IS POSSIBLE TO HAVE MANY RECIVER FOR A SINGLE MESSAGE
					ACLMessage request_data = new ACLMessage(ACLMessage.REQUEST);
					for (int i = 0; i < serviceProviders.size(); i++) {
						request_data.addReceiver(serviceProviders.get(i));
					}
					request_data.setContent(requestedData.toString());
					//TODO: DO WE NEED DEADLINES 
					// request.setReplyByDate(DEADLINE); you can set deadline here
					myAgent.send(request_data);
				}
			} else if (msg_inform != null) {
				replyList.add(msg_inform);
				numberOfReplies++;
				if (numberOfReplies == serviceProviders.size()) {
					processedResult = processedResult(requestedData, replyList);
					ACLMessage informReasoner = new ACLMessage(ACLMessage.INFORM);
					informReasoner.addReceiver(getMyReasonerAID());
					informReasoner.setContent(processedResult);
					myAgent.send(informReasoner);
				}

			} else if (msg_refuse != null) {
				numberOfReplies++;
				if(numberOfReplies==serviceProviders.size()) {
					if(replyList.isEmpty()) { //if there is no reply send failure
						response = new ACLMessage(ACLMessage.FAILURE);
						response.addReceiver(getMyReasonerAID());
						myAgent.send(response);		
					}else {
						processedResult = processedResult(requestedData, replyList);
						ACLMessage informReasoner = new ACLMessage(ACLMessage.INFORM);
						informReasoner.addReceiver(getMyReasonerAID());
						informReasoner.setContent(processedResult);
						myAgent.send(informReasoner);
					}
				}
			} else {
				block();
			}
		}
	}

	/**
	 * This Behavior is to provide information for other negotiator agents
	 * 
	 * @author fd
	 *
	 */
	protected class ParticipantBehaviour extends CyclicBehaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private AID senderAgent;
		private SHSensors requestedData;
		private List<AID> localDataProviders;
		private String processedResult;
		private int numberOfReplies;
		private List<ACLMessage> replyList;
		MessageTemplate mt_request = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
		MessageTemplate mt_inform = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
		
		public ParticipantBehaviour() {
		}

		@Override
		public void action() {
			ACLMessage msg_request = myAgent.receive(mt_request);
			ACLMessage msg_inform = myAgent.receive(mt_inform);
			if(msg_request != null) {
				replyList = new ArrayList<ACLMessage>();
				numberOfReplies =0;
				senderAgent = msg_request.getSender();
				requestedData = SHSensors.valueOf(msg_request.getContent());
				localDataProviders = getLocalDataProviders(getAID(), requestedData, myAgent);
				if(localDataProviders.isEmpty()) {
					ACLMessage responseMSG = new ACLMessage(ACLMessage.REFUSE);
					responseMSG.addReceiver(senderAgent);
					myAgent.send(responseMSG);
				}else {
					ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
					//TODO: CHECK LOOP LESS THAN OR EQUAL TO OR LESS THAN
					for (int i = 0; i < localDataProviders.size(); i++) {
						request.addReceiver(localDataProviders.get(i));
					}
					request.setContent(requestedData.toString());
					//TODO: DO WE NEED DEADLINES
					//request.setReplyByDate(date);
					myAgent.send(request);
				}
			}else if(msg_inform != null) {
				replyList.add(msg_inform);
				numberOfReplies++;
				if (numberOfReplies == localDataProviders.size()) {
					processedResult = processedResult(requestedData, replyList);
					ACLMessage responseMSG = new ACLMessage(ACLMessage.INFORM);
					responseMSG.addReceiver(senderAgent);
					responseMSG.setContent(processedResult);
					myAgent.send(responseMSG);
				}
			}else {
				block();
			}
		}
	}

	
	
	
	/**
	 * register global services which can be located by the negotiator agents The
	 * service id of globally accessible services are constructed as SERIVICE_NAME
	 * Only
	 * 
	 * @param agentID
	 * @param services
	 */
	protected void registerGlobalServices(AID agentID, ISHSensors[] sensors) {
		String sensor;
		ServiceDescription sd;
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(agentID);
		for (int i = 0; i < sensors.length; i++) {
			sensor = sensors[i].toString();
			sd = new ServiceDescription();
			sd.setName(sensor);
			sd.setType(sensor);
			dfd.addServices(sd);
		}
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}
	
	
	
	/**
	 * The global services can be located with service id (service name) without
	 * location
	 * 
	 * @param sensor
	 * @param myAgent
	 * @return
	 */
	protected List<AID> getGlobalServiceProviders(SHSensors sensor, Agent myAgent) {
		List<AID> serviceProviders = new ArrayList<AID>();
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(sensor.toString());
		sd.setName(sensor.toString());
		template.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(myAgent, template);
			if (result != null) {
				for (int i = 0; i < result.length; i++) {
					if (!(myAgent.getAID().equals(result[i].getName()))) {
						serviceProviders.add(result[i].getName());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serviceProviders;
	}

	

	/**
	 * Look for local(room level) service provider agents in the JADE Yellow page
	 * 
	 * @param agentID The ID of the agent look for services
	 * @param service The required service
	 * @return
	 */
	protected List<AID> getLocalDataProviders(AID agentID, SHSensors sensor, Agent myAgent) {
		List<AID> serviceProviders = new ArrayList<AID>();
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getServiceName(agentID, sensor));
		sd.setType(getServiceName(agentID, sensor));
		template.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(myAgent, template);
			if ((result != null) && (result.length > 0)) {
				for (int i = 0; i < result.length; i++) {
					serviceProviders.add(result[i].getName());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
 		return serviceProviders;
	}
	
	
	/**
	 * Given agent id retrieve the agent location
	 * 
	 * @return
	 */
	private String getAgentLocation(AID agentID) {
		return agentID.getName().split("_")[0];
	}

	private String getServiceName(AID agentID, SHSensors sensor) {
		return getAgentLocation(agentID)+"_" +sensor.toString();
	}

	
	/**
	 * Process message
	 * 
	 * @param msgList
	 * @return
	 */
	protected String processTemp(List<ACLMessage> msgList) {
		return msgList.get(0).toString();
	}

	
	
	protected String processedResult(SHSensors sensor, List<ACLMessage> responseList) {
		String processedResult="";
		switch (sensor) {
		case INHABITANTACTIVITY:
			  processedResult = processInhabitantActivity(responseList);
			break;
		case  LUMINOSITY:
			processedResult = processLuminosity(responseList);
			break;
		case INHABITANTLOCALIZATION:
			processedResult = processInhabitatantLocation(responseList);
			break;
		case TEMPERATURE:
				processedResult = processTemp(responseList);
			break;
		default:
			break;
		}
		return processedResult;
	}
	

	
	/**
	 * Process message
	 * 
	 * @param msgList
	 * @return
	 */
	protected String processLuminosity(List<ACLMessage> msgList) {
		return null;
	}

	
	/**
	 * Process message
	 * 
	 * @param msgList
	 * @return
	 */
	protected String processInhabitatantLocation(List<ACLMessage> msgList) {
		return null;
	}
	/**
	 * Process message
	 * 
	 * @param msgList
	 * @return
	 */
	protected String processInhabitantActivity(List<ACLMessage> msgList) {
		return null;
	}
	/**
	 * Process message
	 * 
	 * @param msgList
	 * @return
	 */
	protected String processLight(List<ACLMessage> msgList) {
		return null;
	}
	
}
