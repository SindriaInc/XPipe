#!/usr/bin/env bash

APP_NAME=iam-v1-api-collector

docker exec -it ${APP_NAME} su sindria -c "/usr/local/bin/composer install"