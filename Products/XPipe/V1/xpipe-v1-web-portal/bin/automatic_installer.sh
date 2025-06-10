#!/usr/bin/env bash

# Admin configuration
ADMIN_FIRST_NAME=Carbon
ADMIN_LAST_NAME=User
ADMIN_EMAIL=carbon.user@sindria.org
ADMIN_USERNAME=carbon.user
ADMIN_PASSWORD=admin123

# App configuration
APP_NAME=xpipe-v1-web-portal

bash bin/magento_setup.sh ${ADMIN_FIRST_NAME} ${ADMIN_LAST_NAME} ${ADMIN_EMAIL} ${ADMIN_USERNAME} ${ADMIN_PASSWORD}

COMMAND_01="composer install"
COMMAND_02="php bin/magento setup:upgrade"
COMMAND_03="php bin/magento setup:di:compile"
COMMAND_04="php bin/magento setup:static-content:deploy it_IT en_US -f"
COMMAND_05="php bin/magento indexer:reindex"
COMMAND_06="php bin/magento deploy:mode:set developer"
COMMAND_07="php bin/magento cache:flush"

docker exec -t ${APP_NAME} ${COMMAND_01}
docker exec -t ${APP_NAME} ${COMMAND_02}
docker exec -t ${APP_NAME} ${COMMAND_03}
docker exec -t ${APP_NAME} ${COMMAND_04}
docker exec -t ${APP_NAME} ${COMMAND_05}
docker exec -t ${APP_NAME} ${COMMAND_06}
docker exec -t ${APP_NAME} ${COMMAND_07}