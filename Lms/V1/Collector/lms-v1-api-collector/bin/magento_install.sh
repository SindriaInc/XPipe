#!/usr/bin/env bash

APP_NAME=lms-v1-api-collector

docker exec -it ${APP_NAME} su sindria -c "composer create-project --repository-url=https://repo.magento.com/ magento/project-community-edition ."