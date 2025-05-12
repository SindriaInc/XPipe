#!/usr/bin/env bash

CONTAINER_NAME=xp-pipelines

docker exec -t ${CONTAINER_NAME} su sindria -c "mvn compile; mvn package"
#docker exec -t ${CONTAINER_NAME} su sindria -c "java -jar /var/www/app/target/xp-policy-0.1.0.jar"