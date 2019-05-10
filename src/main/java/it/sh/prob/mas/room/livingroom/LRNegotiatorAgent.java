package it.sh.prob.mas.room.livingroom;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;

public class LRNegotiatorAgent extends Agent{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void setup() {
	
	addBehaviour(new CyclicBehaviour() {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			System.out.println("Living room negotiator AGENT....");
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}
		}
	});
	
	}


}