#!/usr/bin/env bash

APP_NAME=xpipe-ecommerce

docker exec -it ${APP_NAME} su sindria -c "/usr/local/bin/composer install"