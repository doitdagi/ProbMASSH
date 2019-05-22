package it.sh.prob.mas.room.livingroom.utilities;

import it.sh.prob.mas.ISHSensors;

public enum LivingroomSensors implements ISHSensors{
	activity, // INHABITANTACTIVITY,
	luminosity, // LUMINOSITY,
	location; // INHABITANTLOCALIZATION;


	@Override
	public ISHSensors[] getServices() {
		return LivingroomSensors.values();
	}
	
	
	@Override
	public ISHSensors getSerivce(String element) {
		return LivingroomSensors.valueOf(element);
	}

}
