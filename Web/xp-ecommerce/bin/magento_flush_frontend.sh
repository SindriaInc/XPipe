#!/usr/bin/env bash

APP_NAME=xp-ecommerce

docker exec -t ${APP_NAME} su sindria -c "rm -r pub/static/*/*"
docker exec -t ${APP_NAME} su sindria -c "rm -r var/view_preprocessed/*"
docker exec -t ${APP_NAME} su sindria -c "php bin/magento cache:clean"