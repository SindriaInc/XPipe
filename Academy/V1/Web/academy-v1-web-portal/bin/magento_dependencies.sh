#!/usr/bin/env bash

APP_NAME=academy-v1-web-portal

docker exec -it ${APP_NAME} su sindria -c "/usr/local/bin/composer install"