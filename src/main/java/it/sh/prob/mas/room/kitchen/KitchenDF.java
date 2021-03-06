package it.sh.prob.mas.room.kitchen;

import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.df;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class KitchenDF extends df {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void setup() {
		super.setup();
		this.setDescriptionOfThisDF(getSubdfDescription());
		try {
			this.addParent(getDefaultDF(), getDescriptionOfThisDF());
			DFService.register(this, getDefaultDF(), this.getDescriptionOfThisDF());
		
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static DFAgentDescription getSubdfDescription() {
		DFAgentDescription dad = new DFAgentDescription();
 		ServiceDescription sd = new ServiceDescription();
		sd.setName("subdf");
		sd.setType("fipa-df");
		dad.addServices(sd);
		return dad;
	}
	
}
