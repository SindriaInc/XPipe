#!/usr/bin/env bash

CONTAINER_NAME=blog
NANOREST_VERSION=1.0.0


docker exec -t ${CONTAINER_NAME} bash -c "mvn install:install-file \
   -Dfile=libs/nanorest-${NANOREST_VERSION}.jar \
   -DgroupId=org.sindria.xpipe.core.lib \
   -DartifactId=nanorest \
   -Dversion=${NANOREST_VERSION} \
   -Dpackaging=jar \
   -DgeneratePom=true"


docker exec -t ${CONTAINER_NAME} bash -c "mvn compile; mvn package"
#docker exec -t ${CONTAINER_NAME} /bin/bash -c "java -jar target/${CONTAINER_NAME}-${APP_VERSION}.jar"