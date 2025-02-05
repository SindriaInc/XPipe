#!/usr/bin/env bash

CONTAINER_NAME=nanoREST
APP_VERSION=1.0.0

docker exec -t ${CONTAINER_NAME} bash -c "mvn compile; mvn package"
#docker exec -t ${CONTAINER_NAME} /bin/bash -c "java -jar target/${CONTAINER_NAME}-${APP_VERSION}.jar"

echo "Deploy local"
cp /Users/lucapitzoi/XPipe/Lib/nanoREST/src/target/nanoREST-${APP_VERSION}.jar /Users/lucapitzoi/XPipe/Lib/blog/src/libs/nanoREST-${APP_VERSION}.jar
echo "Done"