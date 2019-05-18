package it.sh.prob.mas.room.kitchen.utilities;

import it.sh.prob.mas.ISHSensors;

public enum KitchenSensors implements ISHSensors{
	   INHABITANTACTIVITY,
	   LUMINOSITY,
	   INHABITANTLOCALIZATION;

	@Override
	public ISHSensors[] getServices() {
		return KitchenSensors.values();
	}

	

	@Override
	public ISHSensors getSerivce(String element) {
		return KitchenSensors.valueOf(element);
	}

}
