package it.sh.prob.mas;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.sh.prob.mas.utilites.SHServices;
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
	protected void registerRelevantSHServices(AID agentID, List<SHServices> services) {
		String service;
		ServiceDescription sd;
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(agentID);
		for (int i = 0; i < services.size(); i++) {
			service = services.get(i).toString();
			sd = new ServiceDescription();
			sd.setName(service);
			sd.setType(service);
			dfd.addServices(sd);
		}
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
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
	protected List<AID> getDeviceServiceProviders(SHServices service, Agent myAgent) {
		List<AID> serviceProviders = new ArrayList<AID>();
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(service.toString());
		sd.setName(service.toString());
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
			System.out.println(serviceProviders.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serviceProviders;
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
