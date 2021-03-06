package it.sh.prob.mas;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.introspection.AMSSubscriber;
import jade.domain.introspection.BornAgent;
import jade.domain.introspection.Event;
import jade.domain.introspection.IntrospectionVocabulary;

public abstract class SHAgent extends Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	protected static final String PREFIX = "::";


	protected static final String POSTFIX = ").";
	
	protected abstract List<String> getSHService();

	/**
	 * MSG Reply deadline
	 */

	protected static Date DEADLINE = new Date(System.currentTimeMillis() + 10000);

	protected class RegisterSHServices extends AMSSubscriber {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private AID df_AID;

		public RegisterSHServices(AID df_AID) {
			this.df_AID = df_AID;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void installHandlers(Map handlers) {
			// Associate an handler to born-agent events
			EventHandler creationsHandler = new EventHandler() {
				/**
				 */
				private static final long serialVersionUID = 1L;

				public void handle(Event ev) {
					BornAgent ba = (BornAgent) ev;
					if (ba.getAgent().getLocalName().equals(df_AID.getLocalName())) {
						// TODO Remove this behaviour after registrations
						registerSHServices(df_AID, getSHService());
					}
				}
			};
			handlers.put(IntrospectionVocabulary.BORNAGENT, creationsHandler);
		}
	}

	protected void registerSHServices(AID df_AID, List<String> services) {
		DFAgentDescription dfd = new DFAgentDescription();
		for (String serviceName : services) {
			ServiceDescription sd = new ServiceDescription();
			sd.setName(serviceName);
			sd.setType(serviceName);
			dfd.addServices(sd);
 		}
		try {
			DFService.register(this, df_AID, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}
	
	
	protected void unregisterSHServices(AID df_AID) {
		DFAgentDescription dfd = new DFAgentDescription();
		for (String serviceName : getSHService()) {
			ServiceDescription sd = new ServiceDescription();
			sd.setName(serviceName);
			sd.setType(serviceName);
			dfd.addServices(sd);
 		}
		try {
			DFService.deregister(this, df_AID, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}
	
 
	/**
	 * Get the list of sensors AIDs, to the required sensor data in the room
	 * 
	 * @param sensorName The name of the sensor that we are looking data provider
	 *                   for
	 * @param myAgent    The agent looking for the data providers
	 * @param localDFID  The local DF address of the room
	 * @return
	 */
	protected List<AID> getSensorDataProviders(String sensorName, Agent myAgent, AID localDFID) {
		List<AID> sensorDataProviders = new ArrayList<AID>();
		DFAgentDescription dad = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setName(sensorName);
		sd.setType(sensorName);
		dad.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(myAgent, localDFID, dad);
			if (result != null) {
				for (int i = 0; i < result.length; i++) {
					// TODO: WHY WE NEED THIS CHECK	
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
	 * 
	 * @return The device controller agent for the given service and location For
	 *         example the kitchen light controller agent id for light service in
	 *         the kitchen
	 */

	/**
	 * Get the the device controller agent AID for the given actuator device
	 * 
	 * @param actuatorName the actuator name
	 * @param myAgent      the agent looking for the device agent
	 * @param localDFID    the DF where we are going to look for the device agent
	 * @return
	 */
	protected AID getDeviceControllerAgent(String actuatorName, Agent myAgent, AID localDFID) {
		AID controllerAgentID = null;
		DFAgentDescription dad = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(actuatorName);
		dad.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(myAgent, localDFID, dad);
			if (result.length != 0) {
				controllerAgentID = result[0].getName();
			}
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return controllerAgentID;
	}

	protected AID toAID(String dfID) {
		AID aid = new AID();
		aid.setLocalName(dfID);
		return aid;
	}
	protected String getNegotiatorAgentID(){
		return null;
	}
	
	protected String generateRandomCertaintiyValues() {
		   DecimalFormat df2 = new DecimalFormat("#.##");
		    return df2.format(Math.random());
	}
	
}
