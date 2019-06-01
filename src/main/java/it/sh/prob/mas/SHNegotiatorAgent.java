package it.sh.prob.mas;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import it.sh.prob.mas.utilites.AgentID;
import it.sh.prob.mas.utilites.SHACLProtocolID;
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

		private MessageTemplate mt_missinginfo_reasoner_req = MessageTemplate.and(
				MessageTemplate.MatchSender(getMyReasonerAID()),
				MessageTemplate.and(
						MessageTemplate.MatchProtocol(SHACLProtocolID.MISSINGDATA_REQUEST_FROM_REASONER_TO_NEGOTIATOR),
						MessageTemplate.MatchPerformative(ACLMessage.REQUEST)));

		private MessageTemplate mt_missingdata_refuse = MessageTemplate.and(
				MessageTemplate.MatchProtocol(SHACLProtocolID.MISSINGDATA_REFUSE_FROM_NEGOTIATOR_TO_NEGOTIATOR),
				MessageTemplate.MatchPerformative(ACLMessage.REFUSE));

		private MessageTemplate mt_missingdata_inform = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.INFORM),
				MessageTemplate.MatchProtocol(SHACLProtocolID.MISSINGDATA_INFORM_NEGOTIATOR_TO_NEGOTIATOR));

		private MessageTemplate mt_reasoning_request = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
				MessageTemplate.MatchProtocol(SHACLProtocolID.REASONING_REQUEST_FROM_REASONER_TO_NEGOTIATOR));
		private MessageTemplate mt_reasoner_result = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.INFORM),
				MessageTemplate.MatchProtocol(SHACLProtocolID.REASONING_RESULT_FROM_NEGOTIATIOR_TO_NEGOTITATOR));

		public InitiatorBehaviour() {
		}

		@Override
		public void action() {
			ACLMessage msg_missingdata_reasoner_req = myAgent.receive(mt_missinginfo_reasoner_req);
			ACLMessage msg_missinginfo_refuse = myAgent.receive(mt_missingdata_refuse);
			ACLMessage msg_missingdata_inform = myAgent.receive(mt_missingdata_inform);

			ACLMessage msg_reasoning_request = myAgent.receive(mt_reasoning_request);
			ACLMessage msg_reasoning_result = myAgent.receive(mt_reasoner_result);

			if (msg_missingdata_reasoner_req != null) {
				requestedData = SHSensors.valueOf(msg_missingdata_reasoner_req.getContent());
				numberOfReplies = 0; // reset no of replies for each request
				replyList = new ArrayList<ACLMessage>();
				serviceProviders = getSensorDataProviders(requestedData.toString(), myAgent,
						toAID(AgentID.HOUSE_DF_AID));
				

				if (serviceProviders.isEmpty()) {
					response = new ACLMessage(ACLMessage.FAILURE);
					response.addReceiver(getMyReasonerAID());
					response.setProtocol(SHACLProtocolID.MISSINGDATA_FAILURE_NEGOTIATOR_TO_REASOSNER);
					myAgent.send(response);
				} else {
					ACLMessage request_data = new ACLMessage(ACLMessage.REQUEST);

					for (int i = 0; i < serviceProviders.size(); i++) {
						request_data.setProtocol(SHACLProtocolID.MISSINGDATA_REQUEST_NEGOTIATOR_TO_NEGOTIATOR);
						request_data.addReceiver(serviceProviders.get(i));
					}
					request_data.setContent(requestedData.toString());
					// TODO: DO WE NEED DEADLINES
					// request.setReplyByDate(DEADLINE); you can set deadline here
					myAgent.send(request_data);
				}

			} else if (msg_missingdata_inform != null) {
				replyList.add(msg_missingdata_inform);
				numberOfReplies++;
				if (numberOfReplies == serviceProviders.size()) {
					processedResult = processedResult(requestedData, replyList);
					ACLMessage informReasoner = new ACLMessage(ACLMessage.INFORM);
					informReasoner.addReceiver(getMyReasonerAID());
					informReasoner.setContent(processedResult);
					informReasoner.setProtocol(SHACLProtocolID.MISSINGDATA_INFORM_NEGOTIATOR_TO_REASONER);
					myAgent.send(informReasoner);
				}

			} else if (msg_missinginfo_refuse != null) {
				numberOfReplies++;
				if (numberOfReplies == serviceProviders.size()) {
					if (replyList.isEmpty()) { // if there is no reply send failure
						response = new ACLMessage(ACLMessage.FAILURE);
						response.setProtocol(SHACLProtocolID.MISSINGDATA_FAILURE_NEGOTIATOR_TO_REASOSNER);
						response.addReceiver(getMyReasonerAID());
						myAgent.send(response);
 					} else {
						processedResult = processedResult(requestedData, replyList);
						ACLMessage informReasoner = new ACLMessage(ACLMessage.INFORM);
						informReasoner.addReceiver(getMyReasonerAID());
						informReasoner.setContent(processedResult);
						informReasoner.setProtocol(SHACLProtocolID.MISSINGDATA_INFORM_NEGOTIATOR_TO_REASONER);
						myAgent.send(informReasoner);
					}
				}
			} else if (msg_reasoning_request != null) {
				serviceProviders = getSensorDataProviders(SHParameters.REASONING, myAgent, toAID(AgentID.HOUSE_DF_AID));
				if (serviceProviders.isEmpty()) {
					response = new ACLMessage(ACLMessage.FAILURE);
					response.addReceiver(getMyReasonerAID());
					myAgent.send(response);
				} else {
					Random r = new Random();
					ACLMessage request_reasoning = new ACLMessage(ACLMessage.REQUEST);
					int ran = r.nextInt(serviceProviders.size());
					request_reasoning.addReceiver(serviceProviders.get(ran));
					request_reasoning.setContent(msg_reasoning_request.getContent());
					request_reasoning.setProtocol(SHACLProtocolID.REASONING_REQUEST_FROM_NEGOTIATIOR_TO_NEGOTITATOR);
					// TODO: DO WE NEED DEADLINES
					// request.setReplyByDate(DEADLINE); you can set deadline here
					myAgent.send(request_reasoning);
				}

			} else if (msg_reasoning_result != null) {
				ACLMessage result = new ACLMessage(ACLMessage.INFORM);
				result.setContent(msg_reasoning_result.getContent());
				result.addReceiver(getMyReasonerAID());
				result.setProtocol(SHACLProtocolID.MISSINGDATA_INFORM_NEGOTIATOR_TO_REASONER);
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
		private AID sender_nego_Agent;
		private SHSensors requestedData;
		private List<AID> localDataProviders;
		private String processedResult;
		private int numberOfReplies;
		private List<ACLMessage> replyList;

		private MessageTemplate mt_missingdata_request = MessageTemplate.and(
				MessageTemplate.MatchProtocol(SHACLProtocolID.MISSINGDATA_REQUEST_NEGOTIATOR_TO_NEGOTIATOR),
				MessageTemplate.MatchPerformative(ACLMessage.REQUEST));

		private MessageTemplate mt_missingdata_inform = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.INFORM),
				MessageTemplate.MatchProtocol(SHACLProtocolID.MISSINGDATA_INFORM_DEVICE_TO_NEGOTIATOR));

		private MessageTemplate mt_reasoning_request = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
				MessageTemplate.MatchProtocol(SHACLProtocolID.REASONING_REQUEST_FROM_NEGOTIATIOR_TO_NEGOTITATOR));
		private MessageTemplate mt_reasoning_query = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.INFORM),
				MessageTemplate.and(MessageTemplate.MatchSender(getMyReasonerAID()),
						MessageTemplate.MatchProtocol(SHACLProtocolID.REASONING_RESULT_FROM_REASONER_TO_NEGOTIATOR)));

		public ParticipantBehaviour() {
		}

		@Override
		public void action() {
			ACLMessage msg_missingdata_request_from_negotiator = myAgent.receive(mt_missingdata_request);
			ACLMessage msg_missingdata_inform = myAgent.receive(mt_missingdata_inform);
			ACLMessage msg_reasoning_request = myAgent.receive(mt_reasoning_request);
			if (msg_missingdata_request_from_negotiator != null) {
				replyList = new ArrayList<ACLMessage>();
				numberOfReplies = 0;
				sender_nego_Agent = msg_missingdata_request_from_negotiator.getSender();
				requestedData = SHSensors.valueOf(msg_missingdata_request_from_negotiator.getContent());
				localDataProviders = getSensorDataProviders(requestedData.toString(), myAgent, get_Local_df_ID());
				if (localDataProviders.isEmpty()) {
					ACLMessage msg_missingdata_refuse = msg_missingdata_request_from_negotiator.createReply();
					msg_missingdata_refuse.setPerformative(ACLMessage.REFUSE);
					msg_missingdata_refuse
							.setProtocol(SHACLProtocolID.MISSINGDATA_REFUSE_FROM_NEGOTIATOR_TO_NEGOTIATOR);
					myAgent.send(msg_missingdata_refuse);
				} else {
					ACLMessage msg_missingdata_request = new ACLMessage(ACLMessage.REQUEST);
					msg_missingdata_request.setProtocol(SHACLProtocolID.MISSINGDATA_REQUEST_NEGOTIATOR_TO_DEVICE);
					for (int i = 0; i < localDataProviders.size(); i++) {
						msg_missingdata_request.addReceiver(localDataProviders.get(i));
					}
					msg_missingdata_request.setContent(requestedData.toString());
					// TODO: DO WE NEED DEADLINES
					// request.setReplyByDate(date);
					myAgent.send(msg_missingdata_request);
				}
			} else if (msg_missingdata_inform != null) {
				replyList.add(msg_missingdata_inform);
				numberOfReplies++;
				if (numberOfReplies == localDataProviders.size()) {
					processedResult = processedResult(requestedData, replyList);
					ACLMessage msg_missingdata_response = new ACLMessage(ACLMessage.INFORM);
					msg_missingdata_response.addReceiver(sender_nego_Agent);
					msg_missingdata_response.setContent(processedResult);
					msg_missingdata_response.setProtocol(SHACLProtocolID.MISSINGDATA_INFORM_NEGOTIATOR_TO_NEGOTIATOR);
					myAgent.send(msg_missingdata_response);
				}
			} else if (msg_reasoning_request != null) {
				ACLMessage message_to_reasoner = new ACLMessage(ACLMessage.REQUEST);
				message_to_reasoner.addReceiver(getMyReasonerAID());
				message_to_reasoner.setContent(msg_reasoning_request.getContent());
				message_to_reasoner.setProtocol(SHACLProtocolID.REASONING_REQUEST_FROM_NEGOTIATOR_TO_REASONER);
				myAgent.send(message_to_reasoner);
				ACLMessage msg_reasoning_result = myAgent.blockingReceive(mt_reasoning_query);
				ACLMessage message_from_reasoner = msg_reasoning_request.createReply(); // here it can not be null
																						// because we we serve only one
																						// request for the time beign,
				message_from_reasoner.setPerformative(ACLMessage.INFORM);
				message_from_reasoner.setProtocol(SHACLProtocolID.REASONING_RESULT_FROM_NEGOTIATIOR_TO_NEGOTITATOR);
				message_from_reasoner.setContent(msg_reasoning_result.getContent());
				myAgent.send(message_from_reasoner);
			} else {
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
		}else {
			// TODO big todo on merging data from diffrent sources
			return msgList.get(0).getContent();
		}
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
		}else {
			// TODO big todo on merging data from diffrent sources
			return msgList.get(0).getContent();
		}

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
		}else {
			// TODO big todo on merging data from diffrent sources
			return msgList.get(0).getContent();
		}
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
		}else {
			// TODO big todo on merging data from diffrent sources
			return msgList.get(0).getContent();
		}
	}

}
