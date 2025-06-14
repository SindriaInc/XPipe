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
MAGENTO_BASE_URL=https://dev-api-xpipe.sindria.org
MAGENTO_LANGUAGE=en_US
MAGENTO_CURRENCY=EUR
MAGENTO_TIMEZONE=Europe/Rome
MAGENTO_USE_REWRITE=1
MAGENTO_SEARCH_ENGINE=elasticsearch7

# Database configuration
MAGENTO_DB_HOST=crm-v1-api-collector-db.xpipe-dev.svc.cluster.local
MAGENTO_DB_NAME=app
MAGENTO_DB_USER=user
MAGENTO_DB_PASSWORD=secret

# Elasticsearch configuration
ELASTICSEARCH_HOST=crm-v1-api-collector-idx.xpipe-dev.svc.cluster.local
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
