#!/bin/sh

JAR_FILE_NAME=target/scala-2.11/json_diff-assembly-1.0.0.jar

if [ ! -f ${JAR_FILE_NAME} ]; then
	echo "Jar file not present, you may need to run command 'sbt assembly' first"
	
	exit 1
fi

java -Dconfig.file=conf/app.conf -jar $JAR_FILE_NAME 
