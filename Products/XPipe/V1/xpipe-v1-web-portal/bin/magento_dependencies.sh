#!/usr/bin/env bash

APP_NAME=xpipe-v1-web-portal

docker exec -it ${APP_NAME} su sindria -c "/usr/local/bin/composer install"