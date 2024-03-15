#!/usr/bin/env bash

set -e

# Setting Colors
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
NC='\033[0m' #No Color
NOW=$(date "+%Y-%m-%d_%H-%M-%S")


# Validation company name env
if [ "${CYR_COMPANY_NAME}" == "" ]; then
    echo -e "${YELLOW}CYR_COMPANY_NAME env must be provided${NC}"
    echo Abort
    exit 1
fi

# Validation package env
if [ "${CYR_PACKAGE}" == "" ]; then
    echo -e "${YELLOW}CYR_PACKAGE env must be provided${NC}"
    echo Abort
    exit 1
fi


# Run
python /var/www/app/app/main.py