#!/usr/bin/env bash

CONTAINER_NAME=maitenance-v1-rtn-certsroute53
APP_PATH=/var/www/app

docker exec -it ${CONTAINER_NAME} python ${APP_PATH}/app/main.py