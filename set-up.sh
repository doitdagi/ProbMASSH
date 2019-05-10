#!/bin/sh

echo Update python version 
update-alternatives --install /usr/bin/python python /usr/bin/python3.5 1

echo Install Problog
pip3 install problog

echo  Installing maven... 
apt-get install maven 

echo Downloading JADE ...

curl -o jade.zip https://jade.tilab.com/dl.php?file=JADE-bin-4.5.0.zip


unzip jade.zip

echo installing jade into local maven repo

mvn install:install-file -Dfile=./jade/lib/jade.jar -DgroupId=com.tilab.jade -DartifactId=jade -Dversion=4.5.0 -Dpackaging=jar

echo compiling the project as JAR with its dependencies... 

mvn clean compile assembly:single

rm -rf ./jade*
