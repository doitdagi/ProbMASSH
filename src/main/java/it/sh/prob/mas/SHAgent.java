package it.sh.prob.mas;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
 
public class SHAgent extends Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * MSG Reply deadline
	 */

	protected static Date DEADLINE = new Date(System.currentTimeMillis() + 10000);

	/**
	 * register devices services which can be consumed by the reasoner agent  
	 * 
	 * @param agentID
	 * @param services
	 */
	
	/**
	 *  register devices services which can be consumed by the reasoner agent   
	 * @param type
	 * 			type of the required service e.g. BATHROOM_LIGHT
	 * @param ownership
	 * 				Location e.g. BATHROOM
	 * @param agentID
	 */
	protected void registerRelevantSHServices(String type) {
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		dfd.setName(getAID());
		sd.setName(getLocalName()+"_"+type);
		sd.setType(type);
 		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
 			e.printStackTrace();
		}
	}
	

	
	/**
	 * 
	 */
	protected void registerDeviceController(String type){
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getLocalName()+"_"+type);
		sd.setType(type);
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
 			e.printStackTrace();
		}
 	}

	/**
	 * The   services can be located with service id (service name) without
	 * location
	 * 
	 * @param service
	 * @param myAgent
	 * @return
	 */
	
	/**
	 *  Get service provider agents 
	 * @param name
	 * 			name of the required service 	
	 * 					e.g:  BATHROOM_LIGHT
	 * @param type
	 * 			type of the required service e.g. LIGHT
	 * @param ownership
	 * 				Location e.g. BATHROOM
	 * @param myAgent
	 * 				the agent 
	 * @return
	 */
	protected List<AID> getDeviceServiceProviders(String type, Agent myAgent) {
		List<AID> serviceProviders = new ArrayList<AID>();
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(type);
		template.addServices(sd);
		Agent t = myAgent;
		try {
			DFAgentDescription[] result = DFService.search(myAgent, template);
			System.out.println("result size"+result.length);
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
	 * Formulate the service name given its
	 * @param location
	 * @param service
	 * @return
	 */
	protected String getServiceName(String location, String service) {
		return location+"_"+service;
	}
	
	
	
	protected String parseProblogOutput(Stream<String> problogOutput) {
		String result = "";
		Double value = 0.0;
		Double newValue;
		for (Object s : problogOutput.toArray()) {
			String line = (String) s;
			newValue = Double.valueOf(line.split(":")[0].trim());
			if(newValue>value) {
				value = newValue;
				result = line.split(":")[0].trim();
			}
		}
		return result;
	}
	
	
	/**
	 * 
	 * @return
	 *  		The device controller agent for the given service and location
	 *  		For example the kitchen light controller agent id for light service in the kitchen 
	 */
	protected AID getDeviceControllerAgent(String type, Agent myAgent) {
		AID controllerAgentID = null ;
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(type);
		template.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(myAgent, template);
			if (result != null) {
 				controllerAgentID = result[0].getName();
			}
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return controllerAgentID;
	}

//	// service name bedroom_temp
//	// agent name agentname_location
//
//	/**
//	 * Register Smart home service for the given agent
//	 * 
//	 * @param agentID  the agent id
//	 * @param services list of supported services by the agent
//	 */
//	protected void registerLocalServices(AID agentID, List<SHServices> services) {
//		ServiceDescription sd;
//		DFAgentDescription dfd = new DFAgentDescription();
//		dfd.setName(agentID);
//		for (int i = 0; i < services.size(); i++) {
//			sd = new ServiceDescription();
//			sd.setName(getServiceName(agentID, services.get(i)));
//			sd.setType(getServiceName(agentID, services.get(i)));
//			dfd.addServices(sd);
//		}
//		try {
//			DFService.register(this, dfd);
//		} catch (FIPAException fe) {
//			fe.printStackTrace();
//		}
//	}
//
//	/**
//	 * Look for local(room level) service provider agents in the JADE Yellow page
//	 * 
//	 * @param agentID The ID of the agent look for services
//	 * @param service The required service
//	 * @return
//	 */
//	protected List<AID> getLocalServiceProviders(AID agentID, SHServices service, Agent myAgent) {
//		List<AID> serviceProviders = new ArrayList<AID>();
//		DFAgentDescription template = new DFAgentDescription();
//		ServiceDescription sd = new ServiceDescription();
//		sd.setName(getServiceName(agentID, service));
//		sd.setType(getServiceName(agentID, service));
//		template.addServices(sd);
//		try {
//			DFAgentDescription[] result = DFService.search(myAgent, template);
//			if ((result != null) && (result.length > 0)) {
//				for (int i = 0; i < result.length; i++) {
//					serviceProviders.add(result[i].getName());
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
// 		return serviceProviders;
//	}
//
//	/**
//	 * Given agent id retrieve the agent location
//	 * 
//	 * @return
//	 */
//	private String getAgentLocation(AID agentID) {
//		return agentID.getName().split("_")[0];
//	}
//
//	private String getServiceName(AID agentID, SHServices service) {
//		return getAgentLocation(agentID)+"_" +service.toString();
//	}

	
}
