#!/usr/bin/env bash

if [[ -z "$1" ]]; then
    echo "Provide admin first name as first argument (eg. Mario)"
    exit 1
fi

if [[ -z "$2" ]]; then
    echo "Provide a admin last name as second argument (eg. Rossi)"
    exit 1
fi

if [[ -z "$3" ]]; then
    echo "Provide a admin email as third argument (eg. mario.rossi@sindria.org)"
    exit 1
fi

if [[ -z "$4" ]]; then
    echo "Provide a admin username as fourth argument (eg. mario.rossi)"
    exit 1
fi

if [[ -z "$5" ]]; then
    echo "Provide a admin password as fifth argument (eg. secret)"
    exit 1
fi

# Admin configuration
ADMIN_FIRST_NAME=$1
ADMIN_LAST_NAME=$2
ADMIN_EMAIL=$3
ADMIN_USERNAME=$4
ADMIN_PASSWORD=$5

# Magento General configuration
MAGENTO_BASE_URL=http://localhost:8080
MAGENTO_LANGUAGE=en_US
MAGENTO_CURRENCY=EUR
MAGENTO_TIMEZONE=Europe/Rome
MAGENTO_USE_REWRITE=1
MAGENTO_SEARCH_ENGINE=elasticsearch7

# Database configuration
MAGENTO_DB_HOST=172.16.10.202
MAGENTO_DB_NAME=app
MAGENTO_DB_USER=user
MAGENTO_DB_PASSWORD=secret

# Elasticsearch configuration
ELASTICSEARCH_HOST=172.16.10.203
ELASTICSEARCH_PORT=9200

# App configuration
APP_NAME=lab-v1-api-collector

COMMAND_01="php -dmemory_limit=6G bin/magento setup:install --base-url=${MAGENTO_BASE_URL} --db-host=${MAGENTO_DB_HOST} --db-name=${MAGENTO_DB_NAME} --db-user=${MAGENTO_DB_USER} --db-password=${MAGENTO_DB_PASSWORD} --admin-firstname=${ADMIN_FIRST_NAME} --admin-lastname=${ADMIN_LAST_NAME} --admin-email=${ADMIN_EMAIL} --admin-user=${ADMIN_USERNAME} --admin-password=${ADMIN_PASSWORD} --language=${MAGENTO_LANGUAGE} --currency=${MAGENTO_CURRENCY} --timezone=${MAGENTO_TIMEZONE} --use-rewrites=${MAGENTO_USE_REWRITE} --search-engine=${MAGENTO_SEARCH_ENGINE} --elasticsearch-host=${ELASTICSEARCH_HOST} --elasticsearch-port=${ELASTICSEARCH_PORT}"
COMMAND_02="php bin/magento module:disable Magento_TwoFactorAuth"
COMMAND_03="php bin/magento config:set system/security/max_session_size_admin 0"
COMMAND_04="php bin/magento security:recaptcha:disable-for-user-login"
COMMAND_05="php bin/magento security:recaptcha:disable-for-user-forgot-password"
COMMAND_06="bin/magento module:disable Magento_PageCache Magento_FullPageCache"
COMMAND_07="bin/magento config:set system/full_page_cache/caching_application 0"
COMMAND_08="bash /var/www/app/bin/frontname.sh"
#COMMAND_09="php bin/magento pipe:users:rehash-passwords"

docker exec -t ${APP_NAME} ${COMMAND_01}
docker exec -t ${APP_NAME} ${COMMAND_02}
docker exec -t ${APP_NAME} ${COMMAND_03}
docker exec -t ${APP_NAME} ${COMMAND_04}
docker exec -t ${APP_NAME} ${COMMAND_05}
docker exec -t ${APP_NAME} ${COMMAND_06}
docker exec -t ${APP_NAME} ${COMMAND_07}
docker exec -t ${APP_NAME} ${COMMAND_08}
#docker exec -t ${APP_NAME} ${COMMAND_09}