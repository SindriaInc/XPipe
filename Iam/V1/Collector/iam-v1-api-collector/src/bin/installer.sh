#!/usr/bin/env bash

set -e

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

# Cloud configuration
CLOUD_BASE_URL=$1
CLOUD_NAMESPACE=$2
# Admin configuration
ADMIN_FIRST_NAME=Carbon
ADMIN_LAST_NAME=User
ADMIN_EMAIL=carbon.user@sindria.org
ADMIN_USERNAME=carbon.user
ADMIN_PASSWORD=admin123

# Composer install
echo -e "${BLUE}Running composer install...${NC}"
composer install --working-dir /var/www/app
echo

# Magento install
echo -e "${BLUE}Running Magento setup:install...${NC}"
bash /var/www/app/bin/cloud.sh ${CLOUD_BASE_URL} ${CLOUD_NAMESPACE} ${ADMIN_FIRST_NAME} ${ADMIN_LAST_NAME} ${ADMIN_EMAIL} ${ADMIN_USERNAME} ${ADMIN_PASSWORD}
echo

# Magento Post install
echo -e "${BLUE}Running Magento setup:upgrade...${NC}"
php bin/magento setup:upgrade
echo

echo -e "${BLUE}Running Magento setup:di:compile...${NC}"
php bin/magento setup:di:compile
echo

echo -e "${BLUE}Running Magento setup:static-content:deploy...${NC}"
php bin/magento setup:static-content:deploy it_IT en_US -f
echo

echo -e "${BLUE}Running Magento indexer:reindex...${NC}"
php bin/magento indexer:reindex
echo

echo -e "${BLUE}Running Magento deploy:mode:set developer...${NC}"
php bin/magento deploy:mode:set developer
echo

echo -e "${BLUE}Running Magento cache:flush...${NC}"
php bin/magento cache:flush
echo
