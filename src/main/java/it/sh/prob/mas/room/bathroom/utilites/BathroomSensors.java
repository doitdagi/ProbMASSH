package it.sh.prob.mas.room.bathroom.utilites;

import it.sh.prob.mas.ISHSensors;

public enum BathroomSensors implements ISHSensors {
   INHABITANTACTIVITY,
   LUMINOSITY,
   INHABITANTLOCALIZATION;

@Override
public BathroomSensors[] getServices() {
 	return BathroomSensors.values();
}

@Override
public ISHSensors getSerivce(String element) {
	return BathroomSensors.valueOf(element);
}
   
}
