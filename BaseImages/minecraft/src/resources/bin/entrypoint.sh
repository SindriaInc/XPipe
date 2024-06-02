#!/usr/bin/env bash

screen -dmS mc "java -javaagent:/opt/jmx_prometheus_javaagent.jar=$JMX_EXPORTER_PORT:/opt/config.yaml $JAVA_OPTS -jar server.jar nogui"