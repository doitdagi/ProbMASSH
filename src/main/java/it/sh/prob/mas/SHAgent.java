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

	 
	protected void registerSHServices(String serviceName, AID df_AID) {
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getLocalName()+"-"+serviceName);
		sd.setType(serviceName);
 		dfd.addServices(sd);
		try {
			DFService.register(this,df_AID, dfd);
		} catch (FIPAException e) {
 			e.printStackTrace();
		}
	}
	
	
	
	
	/**
	 * TODO: This method can be replace by registerRelevantSHServices
	 */
	protected void registerDeviceController(String type,AID localDFID){
		DFAgentDescription dfd = new DFAgentDescription();
//		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getLocalName()+"_"+type);
		sd.setType(type);
		dfd.addServices(sd);
		try {
			DFService.register(this,localDFID, dfd);
		} catch (FIPAException e) {
 			e.printStackTrace();
		}
 	}

	 
	/**
	 *  Get the list of sensors AIDs, to the required sensor data in the room 
	 * @param sensorName
	 * 				The name of the sensor that we are looking data provider for
	 * @param myAgent
	 * 				The agent looking for the data providers 
	 * @param localDFID
	 * 				The local DF address of the room 
	 * @return
	 */
	protected List<AID> getSensorDataProviders(String sensorName, Agent myAgent, AID localDFID) {
		List<AID> sensorDataProviders = new ArrayList<AID>();
		DFAgentDescription dad = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(sensorName);
		dad.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(myAgent, localDFID, dad);
			if (result != null) {
				for (int i = 0; i < result.length; i++) {
					//TODO: WHY WE NEED THIS CHECK
					if (!(myAgent.getAID().equals(result[i].getName()))) {
						sensorDataProviders.add(result[i].getName());
						
					}
				}
			}
 		} catch (Exception e) {
			e.printStackTrace();
		}
		return sensorDataProviders;
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
	
	/**
	 *  Get the the device controller agent AID for the given actuator device
	 * @param actuatorName
	 * 				the actuator name 
	 * @param myAgent
	 * 				the agent looking for the device agent 
	 * @param localDFID
	 * 				the DF where we are going to look for the device agent 
	 * @return
	 */
	protected AID getDeviceControllerAgent(String actuatorName, Agent myAgent, AID localDFID) {
		AID controllerAgentID = null ;
		DFAgentDescription dad = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(actuatorName);
		dad.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(myAgent, localDFID, dad);
			System.out.println("result length"+ result.length);
			if (result.length != 0) {
 				controllerAgentID = result[0].getName();
 				System.out.println("CONTROLLLER AGEENT IS "+controllerAgentID);
			}
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("controller name :"+controllerAgentID);
		return controllerAgentID;
	}
	
	
	
	protected AID toAID(String dfID) {
		AID aid = new AID();
		aid.setLocalName(dfID);
		return aid;
	}

 	
}
