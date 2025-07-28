#!/usr/bin/env bash

# Setting Colors
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
NC='\033[0m' #No Color
NOW=$(date "+%Y-%m-%d_%H-%M-%S")


if [[ -z "$1" ]]; then
    echo "Provide cloud base url as first argument (eg. https://xpipe.sindria.org | https://cloud-api-iam-xpipe.sindria.org)"
    exit 1
fi

if [[ -z "$2" ]]; then
    echo "Provide cloud kubernetes namespace as second argument (eg. xpipe-cloud)"
    exit 1
fi

if [[ -z "$3" ]]; then
    echo "Provide admin first name as first argument (eg. Mario)"
    exit 1
fi

if [[ -z "$4" ]]; then
    echo "Provide a admin last name as second argument (eg. Rossi)"
    exit 1
fi

if [[ -z "$5" ]]; then
    echo "Provide a admin email as third argument (eg. mario.rossi@sindria.org)"
    exit 1
fi

if [[ -z "$6" ]]; then
    echo "Provide a admin username as fourth argument (eg. mario.rossi)"
    exit 1
fi

if [[ -z "$7" ]]; then
    echo "Provide a admin password as fifth argument (eg. secret)"
    exit 1
fi

# Cloud configuration
CLOUD_BASE_URL=$1
CLOUD_NAMESPACE=$2
# Admin configuration
ADMIN_FIRST_NAME=$3
ADMIN_LAST_NAME=$4
ADMIN_EMAIL=$5
ADMIN_USERNAME=$6
ADMIN_PASSWORD=$7

# Magento General configuration
MAGENTO_BASE_URL=${CLOUD_BASE_URL}
MAGENTO_LANGUAGE=en_US
MAGENTO_CURRENCY=EUR
MAGENTO_TIMEZONE=Europe/Rome
MAGENTO_USE_REWRITE=1
MAGENTO_SEARCH_ENGINE=elasticsearch7

# Database configuration
MAGENTO_DB_HOST=xpipe-v1-web-portal-db.${CLOUD_NAMESPACE}.svc.cluster.local
MAGENTO_DB_NAME=app
MAGENTO_DB_USER=user
MAGENTO_DB_PASSWORD=secret

# Elasticsearch configuration
ELASTICSEARCH_HOST=xpipe-v1-web-portal-idx.${CLOUD_NAMESPACE}.svc.cluster.local
ELASTICSEARCH_PORT=9200

php -dmemory_limit=6G bin/magento setup:install --base-url=${MAGENTO_BASE_URL} --db-host=${MAGENTO_DB_HOST} --db-name=${MAGENTO_DB_NAME} --db-user=${MAGENTO_DB_USER} --db-password=${MAGENTO_DB_PASSWORD} --admin-firstname=${ADMIN_FIRST_NAME} --admin-lastname=${ADMIN_LAST_NAME} --admin-email=${ADMIN_EMAIL} --admin-user=${ADMIN_USERNAME} --admin-password=${ADMIN_PASSWORD} --language=${MAGENTO_LANGUAGE} --currency=${MAGENTO_CURRENCY} --timezone=${MAGENTO_TIMEZONE} --use-rewrites=${MAGENTO_USE_REWRITE} --search-engine=${MAGENTO_SEARCH_ENGINE} --elasticsearch-host=${ELASTICSEARCH_HOST} --elasticsearch-port=${ELASTICSEARCH_PORT}
php bin/magento module:disable Magento_TwoFactorAuth

# Disable session size limit
php bin/magento config:set system/security/max_session_size_admin 0

# Disabled captcha for admin dashboard
php bin/magento security:recaptcha:disable-for-user-login
php bin/magento security:recaptcha:disable-for-user-forgot-password

# Disable Page Cache
bin/magento module:disable Magento_PageCache Magento_FullPageCache
bin/magento config:set system/full_page_cache/caching_application 0

# Patch backend frontname
bash /var/www/app/bin/frontname.sh
