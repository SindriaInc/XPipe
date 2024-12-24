#!/usr/bin/env bash

CONTAINER_NAME=blog
APP_VERSION=1.0.0


docker exec -t ${CONTAINER_NAME} bash -c "mvn install:install-file \
   -Dfile=libs/nanoREST-${APP_VERSION}.jar \
   -DgroupId=org.sindria.xpipe.lib \
   -DartifactId=nanoREST \
   -Dversion=${APP_VERSION} \
   -Dpackaging=jar \
   -DgeneratePom=true"


docker exec -t ${CONTAINER_NAME} bash -c "mvn compile; mvn package"
#docker exec -t ${CONTAINER_NAME} /bin/bash -c "java -jar target/${CONTAINER_NAME}-${APP_VERSION}.jar"