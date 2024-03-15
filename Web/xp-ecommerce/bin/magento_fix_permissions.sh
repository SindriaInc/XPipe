#!/usr/bin/env bash

APP_NAME=xp-ecommerce

docker exec -it ${APP_NAME} su sindria -c "find . -type f -exec chmod 664 {} \;"
docker exec -it ${APP_NAME} su sindria -c "find . -type d -exec chmod 775 {} \;"
docker exec -it ${APP_NAME} su sindria -c "find var pub/static pub/media app/etc -type f -exec chmod g+w {} \;"
docker exec -it ${APP_NAME} su sindria -c "find var pub/static pub/media app/etc -type d -exec chmod g+ws {} \;"
docker exec -it ${APP_NAME} su sindria -c "chmod u+x bin/magento"
docker exec -it ${APP_NAME} su sindria -c "chown -R sindria:sindria /var/www/app"