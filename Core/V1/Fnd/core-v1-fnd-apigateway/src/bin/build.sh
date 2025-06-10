#!/usr/bin/env bash

CONTAINER_NAME=xpipe-gateway-v1

docker exec -t ${CONTAINER_NAME} mvn package
#docker exec -t ${CONTAINER_NAME} su sindria -c "java -jar /var/www/app/target/xpipe-policies-0.1.0.jar"