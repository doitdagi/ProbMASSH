package it.sh.prob.mas.room.kitchen.utilities;

import it.sh.prob.mas.ISHSensors;

public enum KitchenSensors implements ISHSensors{
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

}
