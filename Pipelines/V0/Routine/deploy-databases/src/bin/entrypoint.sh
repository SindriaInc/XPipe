#!/usr/bin/env bash

set -e

# Setting Colors
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
NC='\033[0m' #No Color
NOW=$(date "+%Y-%m-%d_%H-%M-%S")


if [ "${IAC_MODE}" == "standalone" ]; then

  # Validation blueprint name
  if [ "${BLUEPRINT_NAME}" == "" ]; then
      echo -e "${YELLOW}BLUEPRINT_NAME env must be provided${NC}"
      exit 1
  fi

  # Validation blueprint refer
  if [ "${BLUEPRINT_REFER}" == "" ]; then
      echo -e "${YELLOW}BLUEPRINT_REFER env must be provided${NC}"
      exit 1
  fi

  # Validation blueprint type
  if [ "${BLUEPRINT_TYPE}" == "" ]; then
    BLUEPRINT_TYPE="lightsail"
  fi


  # Setting blueprint values - if deployments.yaml is overwritten by volume this simply doesn't take any effect. (it's a feature not a bug)
  sed -i -E "s|@@NAME@@|${BLUEPRINT_NAME}|g" /var/www/app/config/deployments.yaml
  sed -i -E "s|@@REFER@@|${BLUEPRINT_REFER}|g" /var/www/app/config/deployments.yaml
  sed -i -E "s|@@TYPE@@|${BLUEPRINT_TYPE}|g" /var/www/app/config/deployments.yaml
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

# Run deploy blueprints
python /var/www/app/app/main.py