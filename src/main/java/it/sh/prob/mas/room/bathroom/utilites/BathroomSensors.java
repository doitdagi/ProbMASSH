package it.sh.prob.mas.room.bathroom.utilites;

import java.util.Arrays;

import it.sh.prob.mas.ISHSensors;

public enum BathroomSensors implements ISHSensors {
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
 
	public static  String[] getServiceNames() {
	    return Arrays.toString(BathroomSensors.values()).replaceAll("^.|.$", "").split(", ");
	}

}
