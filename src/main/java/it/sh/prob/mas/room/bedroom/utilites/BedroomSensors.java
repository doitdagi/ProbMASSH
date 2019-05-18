package it.sh.prob.mas.room.bedroom.utilites;

import it.sh.prob.mas.ISHSensors;

public enum BedroomSensors implements ISHSensors{
	   INHABITANTACTIVITY,
	   LUMINOSITY,
	   INHABITANTLOCALIZATION;

	@Override
	public ISHSensors[] getServices() {
		return BedroomSensors.values();
	}

	@Override
	public ISHSensors getSerivce(String element) {
		return BedroomSensors.valueOf(element);
	}

}
