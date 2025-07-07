#!/usr/bin/env bash

CONTAINER_NAME=maintenance-v1-rtn-certsazure
APP_PATH=/var/www/app

docker exec -it ${CONTAINER_NAME} python ${APP_PATH}/app/main.py