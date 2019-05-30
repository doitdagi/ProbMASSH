package it.sh.prob.mas;

public class MASStarter {
	protected static String HOST_ADDRESS = "";
	protected static String HOST_PORT = "";
	protected static boolean isMain = false;
	protected static boolean displayGUI = false;
	protected static boolean ableToReason =false;
	protected static final String REASONING ="reasoning";
	protected static final String GUI = "gui";
	protected static final String MAIN = "main";
	
	
	protected static boolean validIP(String ip) {
		try {
			if (ip == null || ip.isEmpty()) {
				return false;
			}

			String[] parts = ip.split("\\.");
			if (parts.length != 4) {
				return false;
			}

			for (String s : parts) {
				int i = Integer.parseInt(s);
				if ((i < 0) || (i > 255)) {
					return false;
				}
			}
			if (ip.endsWith(".")) {
				return false;
			}

			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	protected static boolean validPort(String port) {
		try {
			if (port == null || port.isEmpty()) {
				return false;
			}
			if (port.length() != 4) {
				return false;
			}
			Integer.parseInt(port);
			return true;
		} catch (Exception e) {
			return false;
		}
	}


	
}
