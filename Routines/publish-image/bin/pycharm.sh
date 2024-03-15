#!/usr/bin/env bash

CONTAINER_NAME=publish-image
APP_PATH=/var/www/app

docker exec -it ${CONTAINER_NAME} python ${APP_PATH}/app/main.py