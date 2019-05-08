#!/bin/sh

#Parameters
#set main for main container any other string o.w

AGENTS=$1
IS_MAIN=$2
#set gui for display any other string o.w
DISPLAY_GUI=$3
#Better to modify these values inside this script, than passing the values from the terminal
HOST_ADDRESS=10.42.00.01
HOST_PORT=1099


#start agent 
if [ "$AGENTS" = "bedroom" ]
then
     echo "starting bedroom agents"
     java -cp  ./target/ProbMASSH.jar it.sh.prob.mas.room.bedroom.BedroomMASStarter  $IS_MAIN $DISPLAY_GUI $HOST_ADDRESS $HOST_PORT
fi

if [ "$AGENTS" = "livingroom" ]
then 
	echo "starting living room agent"
        java -cp  ./target/ProbMASSH.jar it.sh.prob.mas.room.livingroom.LivingroomMASStarter  $IS_MAIN $DISPLAY_GUI $HOST_ADDRESS $HOST_PORT
fi

if [ "$AGENTS" = "bathroom" ]
then 
	echo "starting bathroom agents"
        java -cp  ./target/ProbMASSH.jar it.sh.prob.mas.room.bathroom.BathroomMASStarter  $IS_MAIN $DISPLAY_GUI $HOST_ADDRESS $HOST_PORT
fi

if [ "$AGENTS" = "kitchen" ]
then
	echo "starting kitchen agents"
        java -cp  ./target/ProbMASSH.jar it.sh.prob.mas.room.kitchen.KitchenMASStarter  $IS_MAIN $DISPLAY_GUI $HOST_ADDRESS $HOST_PORT
fi











