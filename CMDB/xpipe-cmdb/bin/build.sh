#!/usr/bin/env bash

CONTAINER_NAME=xpipe-cmdb

docker exec -t ${CONTAINER_NAME} bash -c "cd /var/www/app; mvn package"
#docker exec -t ${CONTAINER_NAME} su sindria -c "java -jar /var/www/app/target/xpipe-policies-0.1.0.jar"