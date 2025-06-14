#!/usr/bin/env bash

APP_NAME=monitoring-v1-api-collector

docker exec -it ${APP_NAME} bash -c "find . -type f -exec chmod 664 {} \;"
docker exec -it ${APP_NAME} bash -c "find . -type d -exec chmod 775 {} \;"
docker exec -it ${APP_NAME} bash -c "find var pub/static pub/media app/etc -type f -exec chmod g+w {} \;"
docker exec -it ${APP_NAME} bash -c "find var pub/static pub/media app/etc -type d -exec chmod g+ws {} \;"
docker exec -it ${APP_NAME} bash -c "chmod u+x bin/magento"
docker exec -it ${APP_NAME} bash -c "chown -R sindria:sindria /var/www/app"