package it.sh.prob.mas;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import it.sh.prob.mas.utilites.AgentID;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public abstract class SHNegotiatorAgent extends SHAgent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected abstract AID getMyReasonerAID();

	protected abstract ISHSensors[] getSupportedServices();

	protected List<String> serviceList = new ArrayList<String>();

	protected void addSupportedServices(String[] services) {
		for (String service : services) {
			serviceList.add(service);
		}
	}

	/**
	 * Check if the reasoning ability is passed as argument, and if so add the
	 * service into the global DF
	 */
	protected void addReasoningAblity() {
		if (getArguments() != null) {
			String reason = (String) getArguments()[0];
			if (reason.equals(SHParameters.REASONING)) {
				serviceList.add(SHParameters.REASONING);
				System.out.println("ADDING REAOING"+getLocalName());
			}
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
		// TODO: CHECK IF THE VARIABLES KEEP THEIR VALUES FOR FUTURE INTERACTION ALSO,
		// THEY SHOULD NOT
		private static final long serialVersionUID = 1L;
		private SHSensors requestedData;
		private List<AID> serviceProviders;
		private String processedResult;
		int numberOfReplies;
		private List<ACLMessage> replyList;
		private ACLMessage response;

		private MessageTemplate mt_re_reasoner = MessageTemplate.and(MessageTemplate.MatchSender(getMyReasonerAID()),
				MessageTemplate.and(MessageTemplate.MatchProtocol("missingdata"),
						MessageTemplate.MatchPerformative(ACLMessage.REQUEST)));
		private MessageTemplate mt_refuse = MessageTemplate.MatchPerformative(ACLMessage.REFUSE);
		private MessageTemplate mt_propose = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
				MessageTemplate.MatchProtocol("Contract-Net"));
		private MessageTemplate mt_reasoner = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
				MessageTemplate.MatchProtocol("Reasoning"));
		private MessageTemplate mt_reasoner_result = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.INFORM),
				MessageTemplate.MatchProtocol("reasoning_result"));


		public InitiatorBehaviour() {
		}

		@Override
		public void action() {
			ACLMessage msg_rq = myAgent.receive(mt_re_reasoner);
			ACLMessage msg_propose = myAgent.receive(mt_propose);
			ACLMessage msg_refuse = myAgent.receive(mt_refuse);
			ACLMessage msg_reasoning = myAgent.receive(mt_reasoner);
			ACLMessage msg_reasoning_result = myAgent.receive(mt_reasoner_result);

			if (msg_rq != null) {
				requestedData = SHSensors.valueOf(msg_rq.getContent());
				numberOfReplies = 0; // reset no of replies for each request
				replyList = new ArrayList<ACLMessage>();
				serviceProviders = getSensorDataProviders(requestedData.toString(), myAgent,
						toAID(AgentID.HOUSE_DF_AID));
				if (serviceProviders.isEmpty()) {
					response = new ACLMessage(ACLMessage.FAILURE);
					response.addReceiver(getMyReasonerAID());
					myAgent.send(response);
				} else {
					ACLMessage request_data = new ACLMessage(ACLMessage.CFP);
					for (int i = 0; i < serviceProviders.size(); i++) {
						request_data.addReceiver(serviceProviders.get(i));
					}
					request_data.setContent(requestedData.toString());
					// TODO: DO WE NEED DEADLINES
					// request.setReplyByDate(DEADLINE); you can set deadline here
					myAgent.send(request_data);
				}

			} else if (msg_propose != null) {
				replyList.add(msg_propose);
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
				if (numberOfReplies == serviceProviders.size()) {
					if (replyList.isEmpty()) { // if there is no reply send failure
						response = new ACLMessage(ACLMessage.FAILURE);
						response.addReceiver(getMyReasonerAID());
						myAgent.send(response);
					} else {
						processedResult = processedResult(requestedData, replyList);
						ACLMessage informReasoner = new ACLMessage(ACLMessage.INFORM);
						informReasoner.addReceiver(getMyReasonerAID());
						informReasoner.setContent(processedResult);
						myAgent.send(informReasoner);
					}
				}
			} else if (msg_reasoning != null) {
 				serviceProviders = getSensorDataProviders(SHParameters.REASONING, myAgent, toAID(AgentID.HOUSE_DF_AID));
				if (serviceProviders.isEmpty()) {
					System.out.println("Reasoners not found on the network");
					response = new ACLMessage(ACLMessage.FAILURE);
					response.addReceiver(getMyReasonerAID());
					myAgent.send(response);
				} else {
					System.out.println("found reasoners..."+serviceProviders.size());
					Random r = new Random();
					ACLMessage request_reasoning = new ACLMessage(ACLMessage.REQUEST);
					int  ran = r.nextInt(serviceProviders.size());
 					request_reasoning.addReceiver(serviceProviders.get(ran));
					request_reasoning.setContent(msg_reasoning.getContent());
					request_reasoning.setProtocol("request_reasoning");
 					// TODO: DO WE NEED DEADLINES
					// request.setReplyByDate(DEADLINE); you can set deadline here
					myAgent.send(request_reasoning);
					System.out.println("hOW MNAY TIMES DO WE NEET");
				}

			} else if (msg_reasoning_result != null) {
				System.out.println("MESSAGE SENT...");
				ACLMessage result = new ACLMessage(ACLMessage.INFORM);
				result.setContent(msg_reasoning_result.getContent());
				result.addReceiver(getMyReasonerAID());
				myAgent.send(result);
			}

			else {
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

		private MessageTemplate mt_cfp = MessageTemplate.and(
				MessageTemplate.not(MessageTemplate.MatchSender(getMyReasonerAID())),
				MessageTemplate.MatchPerformative(ACLMessage.CFP));

		private MessageTemplate mt_inform = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
				MessageTemplate.MatchProtocol("fipa-request"));

		private MessageTemplate mt_reasoning_request = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
				MessageTemplate.MatchProtocol("request_reasoning"));
		private MessageTemplate mt_reasoning_query = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.INFORM),
				MessageTemplate.and(MessageTemplate.MatchSender(getMyReasonerAID()),
						MessageTemplate.MatchProtocol("reasoning_result_XX")));
		public ParticipantBehaviour() {
		}

		@Override
		public void action() {
			ACLMessage msg_cfp = myAgent.receive(mt_cfp);
			ACLMessage msg_inform = myAgent.receive(mt_inform);
			ACLMessage  msg_reasoning_request = myAgent.receive(mt_reasoning_request);
			if (msg_cfp != null) {
				replyList = new ArrayList<ACLMessage>();
				numberOfReplies = 0;
				senderAgent = msg_cfp.getSender();
				requestedData = SHSensors.valueOf(msg_cfp.getContent());
				localDataProviders = getSensorDataProviders(requestedData.toString(), myAgent, get_Local_df_ID());
				if (localDataProviders.isEmpty()) {
					ACLMessage responseMSG = msg_cfp.createReply();
					responseMSG.setPerformative(ACLMessage.REFUSE);
					myAgent.send(responseMSG);
				} else {
					ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
					for (int i = 0; i < localDataProviders.size(); i++) {
						request.addReceiver(localDataProviders.get(i));
					}
					request.setContent(requestedData.toString());
					// TODO: DO WE NEED DEADLINES
					// request.setReplyByDate(date);
					myAgent.send(request);
				}
			} else if (msg_inform != null) {
				replyList.add(msg_inform);
				numberOfReplies++;
				if (numberOfReplies == localDataProviders.size()) {
					processedResult = processedResult(requestedData, replyList);
					ACLMessage responseMSG = new ACLMessage(ACLMessage.PROPOSE);
					responseMSG.addReceiver(senderAgent);
					responseMSG.setContent(processedResult);
					responseMSG.setProtocol("Contract-Net");
					myAgent.send(responseMSG);
				}
			} else if (msg_reasoning_request != null) {
			//TODO: WE WERE SENDING EMPTY MESSAGE
				ACLMessage message_to_reasoner = new ACLMessage(ACLMessage.REQUEST);
				message_to_reasoner.addReceiver(getMyReasonerAID());
				message_to_reasoner.setContent(msg_reasoning_request.getContent());
				message_to_reasoner.setProtocol("external_query");
				myAgent.send(message_to_reasoner);
				ACLMessage msg_reasoning_result = myAgent.blockingReceive(mt_reasoning_query);
				ACLMessage message_from_reasoner = msg_reasoning_request.createReply(); //here it can not be null because we we serve only one request for the time beign,
				message_from_reasoner.setPerformative(ACLMessage.INFORM);
				message_from_reasoner.setProtocol("reasoning_result");
				message_from_reasoner.setContent(msg_reasoning_result.getContent());
				myAgent.send(message_from_reasoner);
			} 
//			else if (msg_reasoning_result != null) {

//				ACLMessage message_from_reasoner = msg_reasoning_request.createReply(); //here it can not be null because we we serve only one request for the time beign,
//				message_from_reasoner.setPerformative(ACLMessage.INFORM);
//				message_from_reasoner.setProtocol("reasoning_result");
//				message_from_reasoner.setContent(msg_reasoning_result.getContent());
//				myAgent.send(message_from_reasoner);
//			}

			else {
				block();
			}
		}
	}

	protected AID get_Local_df_ID() {
		return null;
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
		String processedResult = "";
		switch (sensor) {
		case activity:
			processedResult = processInhabitantActivity(responseList);
			break;
		case luminosity:
			processedResult = processLuminosity(responseList);
			break;
		case location:
			processedResult = processInhabitatantLocation(responseList);
			break;
		case temperature:
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
		if (msgList.size() == 1) {
			return msgList.get(0).getContent();
		}
		// TODO
		return null;
	}

	/**
	 * Process message
	 * 
	 * @param msgList
	 * @return
	 */
	protected String processInhabitatantLocation(List<ACLMessage> msgList) {
		if (msgList.size() == 1) {
			return msgList.get(0).getContent();
		}

		// TODO
		return null;
	}

	/**
	 * Process message
	 * 
	 * @param msgList
	 * @return
	 */
	protected String processInhabitantActivity(List<ACLMessage> msgList) {
		if (msgList.size() == 1) {
			return msgList.get(0).getContent();
		}
		// TODO
		return null;
	}

	/**
	 * Process message
	 * 
	 * @param msgList
	 * @return
	 */
	protected String processLight(List<ACLMessage> msgList) {
		if (msgList.size() == 1) {
			return msgList.get(0).getContent();
		}
		return null;
	}

}
