package it.sh.prob.mas;
/**
 * All the sensors in all the rooms needs to be defined here
 * 
 * 
 * Simply, this is the global copy of all the sensors[data] 
 * in the home
 * 
 * * @author fd
 *
 */
public enum SHSensors implements ISHSensors{
	   INHABITANTACTIVITY,
	   LUMINOSITY,
	   INHABITANTLOCALIZATION,
	   TEMPERATURE;
	
	
	@Override
	public ISHSensors[] getServices() {
		return SHSensors.values();
	}

	@Override
	public ISHSensors getSerivce(String element) {
		return SHSensors.valueOf(element);
	}

}
