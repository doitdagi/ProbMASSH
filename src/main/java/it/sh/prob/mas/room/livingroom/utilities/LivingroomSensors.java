package it.sh.prob.mas.room.livingroom.utilities;

import java.util.Arrays;

import it.sh.prob.mas.ISHSensors;

public enum LivingroomSensors implements ISHSensors {
	activity, // INHABITANTACTIVITY,
	luminosity, // LUMINOSITY,
	location, // INHABITANTLOCALIZATION;
	temperature;

	@Override
	public ISHSensors[] getServices() {
		return LivingroomSensors.values();
	}

	@Override
	public ISHSensors getSerivce(String element) {
		return LivingroomSensors.valueOf(element);
	}

	public static String[] getServiceNames() {
		return Arrays.toString(LivingroomSensors.values()).replaceAll("^.|.$", "").split(", ");
	}

}
