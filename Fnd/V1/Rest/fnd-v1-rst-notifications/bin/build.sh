#!/usr/bin/env bash

CONTAINER_NAME=fnd-v1-rst-notifications
NANOREST_VERSION=2.4.1


docker exec -t ${CONTAINER_NAME} bash -c "mvn install:install-file \
   -Dfile=libs/nanoREST-${NANOREST_VERSION}.jar \
   -DgroupId=org.sindria.xpipe.lib \
   -DartifactId=nanoREST \
   -Dversion=${NANOREST_VERSION} \
   -Dpackaging=jar \
   -DgeneratePom=true"


docker exec -t ${CONTAINER_NAME} bash -c "mvn compile; mvn package"
#docker exec -t ${CONTAINER_NAME} /bin/bash -c "java -jar target/${CONTAINER_NAME}-${APP_VERSION}.jar"