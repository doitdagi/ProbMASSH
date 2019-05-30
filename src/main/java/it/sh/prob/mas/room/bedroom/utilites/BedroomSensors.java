package it.sh.prob.mas.room.bedroom.utilites;

import java.util.Arrays;

import it.sh.prob.mas.ISHSensors;
import it.sh.prob.mas.room.bathroom.utilites.BathroomSensors;

public enum BedroomSensors implements ISHSensors {
	activity, // INHABITANTACTIVITY,
	luminosity, // LUMINOSITY,
	location, // INHABITANTLOCALIZATION;
	temperature;

	@Override
	public BathroomSensors[] getServices() {
		return BathroomSensors.values();
	}

	@Override
	public ISHSensors getSerivce(String element) {
		return BathroomSensors.valueOf(element);
	}

	public static String[] getServiceNames() {
		return Arrays.toString(BedroomSensors.values()).replaceAll("^.|.$", "").split(", ");
	}

}
