#!/usr/bin/env bash

set -e

# Setting Colors
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
NC='\033[0m' #No Color
NOW=$(date "+%Y-%m-%d_%H-%M-%S")


if [ "${IAC_MODE}" == "standalone" ]; then

  # Validation immutable name
  if [ "${IMMUTABLE_NAME}" == "" ]; then
      echo -e "${YELLOW}IMMUTABLE_NAME env must be provided${NC}"
      exit 1
  fi

  # Validation immutable refer
  if [ "${IMMUTABLE_REFER}" == "" ]; then
      echo -e "${YELLOW}IMMUTABLE_REFER env must be provided${NC}"
      exit 1
  fi

  # Validation immutable type
  if [ "${IMMUTABLE_TYPE}" == "" ]; then
    IMMUTABLE_TYPE="lightsail"
  fi

  # Validation immutable bundle
  if [ "${IMMUTABLE_BUNDLE}" == "" ]; then
    IMMUTABLE_BUNDLE="micro_2_0"
  fi

  # Validation immutable zone
  if [ "${IMMUTABLE_ZONE}" == "" ]; then
    IMMUTABLE_ZONE="eu-central-1a"
  fi


  # Setting immutable values - if deployments.yaml is overwritten by volume this simply doesn't take any effect. (it's a feature not a bug)
  sed -i -E "s|@@NAME@@|${IMMUTABLE_NAME}|g" /var/www/app/config/deployments.yaml
  sed -i -E "s|@@REFER@@|${IMMUTABLE_REFER}|g" /var/www/app/config/deployments.yaml
  sed -i -E "s|@@TYPE@@|${IMMUTABLE_TYPE}|g" /var/www/app/config/deployments.yaml
  sed -i -E "s|@@BUNDLE@@|${IMMUTABLE_BUNDLE}|g" /var/www/app/config/deployments.yaml
  sed -i -E "s|@@ZONE@@|${IMMUTABLE_ZONE}|g" /var/www/app/config/deployments.yaml
else
  # Cleanup config directory
  rm -Rf /var/www/app/config
  mkdir -p /var/www/app/config
  # Infra setup
  mkdir -p .setup
  cd .setup
  git clone https://${IAC_GIT_USERNAME}:${IAC_GIT_PASSWORD}@${IAC_GIT_PROVIDER}/${IAC_GIT_NAMESPACE}/${IAC_INFRA_NAME}.git
  cp ${IAC_INFRA_NAME}/config/* ../config
fi

# Run deploy immutables
python /var/www/app/app/main.py