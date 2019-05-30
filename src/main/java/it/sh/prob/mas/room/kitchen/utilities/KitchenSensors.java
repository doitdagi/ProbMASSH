package it.sh.prob.mas.room.kitchen.utilities;

import java.util.Arrays;

import it.sh.prob.mas.ISHSensors;

public enum KitchenSensors implements ISHSensors {
	activity, // INHABITANTACTIVITY,
	luminosity, // LUMINOSITY,
	location, // INHABITANTLOCALIZATION;
	temperature;

	@Override
	public ISHSensors[] getServices() {
		return KitchenSensors.values();
	}

	@Override
	public ISHSensors getSerivce(String element) {
		return KitchenSensors.valueOf(element);
	}

	public static String[] getServiceNames() {
		return Arrays.toString(KitchenSensors.values()).replaceAll("^.|.$", "").split(", ");
	}
}
