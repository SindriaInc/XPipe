#!/usr/bin/env bash

CONTAINER_NAME=xpipe-pipelines-logs-bitbucket
NANOREST_VERSION=1.1.0


docker exec -t ${CONTAINER_NAME} bash -c "mvn install:install-file \
   -Dfile=libs/nanoREST-${NANOREST_VERSION}.jar \
   -DgroupId=org.sindria.xpipe.lib \
   -DartifactId=nanoREST \
   -Dversion=${NANOREST_VERSION} \
   -Dpackaging=jar \
   -DgeneratePom=true"


docker exec -t ${CONTAINER_NAME} bash -c "mvn compile; mvn package"
#docker exec -t ${CONTAINER_NAME} /bin/bash -c "java -jar target/${CONTAINER_NAME}-${NANOREST_VERSION}.jar"