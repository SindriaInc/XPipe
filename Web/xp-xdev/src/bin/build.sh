#!/usr/bin/env bash

CONTAINER_NAME=xp-xdev

docker exec -t ${CONTAINER_NAME} su sindria -c "mvn compile; mvn package"
#docker exec -t ${CONTAINER_NAME} su sindria -c "java -jar /var/www/app/target/xp-xdev-0.1.0.jar"