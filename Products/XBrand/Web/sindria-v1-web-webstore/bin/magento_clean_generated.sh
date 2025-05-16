#!/usr/bin/env bash

APP_NAME=xpipe-ecommerce

docker exec -it ${APP_NAME} bash -c "rm -Rf /var/www/app/generated/code"