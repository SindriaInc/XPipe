#!/usr/bin/env bash

APP_NAME=fnd-v1-api-collector

docker exec -it ${APP_NAME} bash -c "rm -Rf /var/www/app/var/cache"
docker exec -it ${APP_NAME} bash -c "rm -Rf /var/www/app/var/composer_home"
docker exec -it ${APP_NAME} bash -c "rm -Rf /var/www/app/var/log"
docker exec -it ${APP_NAME} bash -c "rm -Rf /var/www/app/var/page_cache"
docker exec -it ${APP_NAME} bash -c "rm -Rf /var/www/app/var/session"
docker exec -it ${APP_NAME} bash -c "rm -Rf /var/www/app/var/tmp"
docker exec -it ${APP_NAME} bash -c "rm -Rf /var/www/app/var/vendor"
docker exec -it ${APP_NAME} bash -c "rm -Rf /var/www/app/var/view_preprocessed"
docker exec -it ${APP_NAME} bash -c "rm -Rf /var/www/app/var/.regenerate.lock"