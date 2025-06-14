#!/usr/bin/env bash

APP_NAME=docs-v1-api-collector

docker exec -it ${APP_NAME} bash -c "rm -Rf /var/www/app/generated/code"