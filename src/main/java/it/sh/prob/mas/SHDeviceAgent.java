package it.sh.prob.mas;

import java.text.DecimalFormat;
import java.util.Map;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.introspection.AMSSubscriber;
import jade.domain.introspection.BornAgent;
import jade.domain.introspection.Event;
import jade.domain.introspection.IntrospectionVocabulary;

public abstract class SHDeviceAgent extends SHAgent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected static final String PREFIX = "::";


	protected static final String POSTFIX = ").";
	
	
	protected abstract String getSHService();
	
	protected abstract String generateRandomDeviceValues();

//	protected abstract String getDeviceSensedData();
	
	protected class RegisterDeviceSensedData extends OneShotBehaviour{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
 	//		registerDeviceSensedData(getDeviceSensedData());
		}
		
	}
	
 

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
 					if(ba.getAgent().getLocalName().equals(df_AID.getLocalName())) {
						//TODO Remove this behaviour after registrations
 						System.out.println("REgistered:"+getSHService()+getLocalName());
 						registerSHServices(getSHService(),df_AID);
  					}
				}
			};
			handlers.put(IntrospectionVocabulary.BORNAGENT, creationsHandler);
		}
		
		
	
	}
	
	
	
	private String generateRandomCertaintiyValues() {
 		   DecimalFormat df2 = new DecimalFormat("#.##");
		    return df2.format(Math.random());
	}
	
	
	protected String formulateReply(String variable){
		return generateRandomCertaintiyValues()+PREFIX+variable+"("+generateRandomDeviceValues()+POSTFIX;
}


	

}
