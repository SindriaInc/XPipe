#!/usr/bin/env bash

APP_NAME=xpipe-v1-web-portal

docker exec -it ${APP_NAME} bash -c "rm -Rf /var/www/app/generated/code"