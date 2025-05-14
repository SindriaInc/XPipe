#!/usr/bin/env bash

CONTAINER_NAME=springboot27-starter

docker exec -t ${CONTAINER_NAME} mvn package
#docker exec -t ${CONTAINER_NAME} su sindria -c "java -jar /var/www/app/target/springboot27-starter-0.1.0.jar"
