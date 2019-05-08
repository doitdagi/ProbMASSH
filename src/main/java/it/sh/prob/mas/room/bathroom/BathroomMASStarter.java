package it.sh.prob.mas.room.bathroom;

import java.util.HashMap;
import java.util.Map;

import it.sh.prob.mas.MASStarter;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.StaleProxyException;

public class BathroomMASStarter extends MASStarter{
	 
	protected static Map<String, String> memberAgents = new HashMap<String, String>();

	/**
	 * 
	 * @param args IS an array of size four args[0] boolean if the container is main
	 *             or not args[1] boolean if we need to display GUI or not args[2]
	 *             String: the host IP address args[3] String: the host port address
	 */
	public static void main(String[] args) {
 		if (args.length != 4) {
			System.err.println("INVALID PARAMETER SIZE");
			return;
		}
		if (args[0].equals("main")) {
			isMain = true;
 		}else {
 			if (validIP(args[2])) {
				HOST_ADDRESS = args[2];
			} else {
				System.err.println("INVALID HOST ADDRESS");
				return;
			}

			if (validPort(args[3])) {
				HOST_PORT = args[3];
			} else {
				System.err.println("INVALID HOST PORT");
				return;
			}
		}
		if (args[1].equals("gui")) {
			displayGUI = true;
		}
		
		initializeMemberAgents();
		Profile p = new ProfileImpl();
		AgentContainer ac;
		if (displayGUI) {
			p.setParameter(Profile.GUI, Boolean.TRUE.toString());
		}

		if (isMain) {
			ac = jade.core.Runtime.instance().createMainContainer(p);
		} else {
			p.setParameter(Profile.MAIN_HOST, HOST_ADDRESS);
			p.setParameter(Profile.MAIN_PORT, HOST_PORT);
			ac = jade.core.Runtime.instance().createAgentContainer(p);
		}

		// add and start all agent

		for (Map.Entry<String, String> agent : memberAgents.entrySet()) {
			try {
				ac.createNewAgent(agent.getKey(), agent.getValue(), null).start();
			} catch (StaleProxyException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Sucessfully started...");
	}

	
	private static void initializeMemberAgents() {
		memberAgents.put("BathNegotiatorAgent", "it.sh.prob.mas.room.bathroom.BathNegotiatorAgent");
		memberAgents.put("BathReasonerAgent", "it.sh.prob.mas.room.bathroom.BathReasonerAgent");
	}


}
