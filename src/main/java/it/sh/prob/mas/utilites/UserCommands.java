package it.sh.prob.mas.utilites;

/**
 * The format of user command should always be 
 * ACTION_STATE_OBJECT/THING
 * E.G TURN_ON_LIGHT
 * So that it will be easy to parse 
 * @author fd
 *
 */
public enum UserCommands {
	TURN_ON_LIGHT,
	HEAT_UP_ROOM
}
