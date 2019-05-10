package it.sh.prob.mas;

import java.text.DecimalFormat;

public abstract class SHDeviceAgent extends SHAgent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected static final String PREFIX = "::";


	protected static final String POSTFIX = ").";
	
	


	
	private String generateRandomCertaintiyValues() {
 		   DecimalFormat df2 = new DecimalFormat("#.##");
		    return df2.format(Math.random());
	}
	
	
	protected String formulateReply(String variable){
		return generateRandomCertaintiyValues()+PREFIX+variable+"("+generateRandomDeviceValues()+POSTFIX;
}


	protected abstract String generateRandomDeviceValues();


}
