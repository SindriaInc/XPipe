#!/usr/bin/env bash

CONTAINER_NAME=nanoREST
APP_VERSION=0.2.0

docker exec -t ${CONTAINER_NAME} su sindria -c "mvn compile; mvn package"
#docker exec -t ${CONTAINER_NAME} /bin/bash -c "java -jar target/${CONTAINER_NAME}-${APP_VERSION}.jar"

echo "Deploy local"
cp ~/Projects/Sindria/sindria-devops-lab/tools/nanoREST/src/target/nanoREST-${APP_VERSION}.jar ~/Projects/Sindria/tennisclubarzachena-team/tca-scoreboards/src/libs/nanoREST-${APP_VERSION}.jar
echo "Done"