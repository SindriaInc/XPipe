#!/usr/bin/env bash

set -e

# Setting Colors
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
NC='\033[0m' #No Color
NOW=$(date "+%Y-%m-%d_%H-%M-%S")


if [ "${IAC_MODE}" == "standalone" ]; then

  # Validation lightsail name
  if [ "${LIGHTSAIL_NAME}" == "" ]; then
      echo -e "${YELLOW}LIGHTSAIL_NAME env must be provided${NC}"
      exit 1
  fi

  # Validation lightsail port
  if [ "${LIGHTSAIL_PORT}" == "" ]; then
      echo -e "${YELLOW}LIGHTSAIL_PORT env must be provided${NC}"
      exit 1
  fi

  # Validation lightsail protocol
  if [ "${LIGHTSAIL_PROTOCOL}" == "" ]; then
    LIGHTSAIL_PROTOCOL="TCP"
  fi

  # Validation lightsail cidr
  if [ "${LIGHTSAIL_CIDR}" == "" ]; then
    LIGHTSAIL_CIDR="0.0.0.0/0"
  fi


  # Setting lightsail values - if security.yaml is overwritten by volume this simply doesn't take any effect. (it's a feature not a bug)
  sed -i -E "s|@@NAME@@|${LIGHTSAIL_NAME}|g" /var/www/app/config/security.yaml
  sed -i -E "s|@@PORT@@|${LIGHTSAIL_PORT}|g" /var/www/app/config/security.yaml
  sed -i -E "s|@@PROTOCOL@@|${LIGHTSAIL_PROTOCOL}|g" /var/www/app/config/security.yaml
  sed -i -E "s|@@CIDR@@|${LIGHTSAIL_CIDR}|g" /var/www/app/config/security.yaml
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

# Run lightsail instance public ports
python /var/www/app/app/main.py