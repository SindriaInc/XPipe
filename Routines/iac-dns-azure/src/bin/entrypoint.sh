#!/usr/bin/env bash

set -e

# Setting Colors
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
NC='\033[0m' #No Color
NOW=$(date "+%Y-%m-%d_%H-%M-%S")


if [ "${IAC_MODE}" == "standalone" ]; then

  # Validation entry name
  if [ "${ENTRY_NAME}" == "" ]; then
      echo -e "${YELLOW}ENTRY_NAME env must be provided${NC}"
      exit 1
  fi

  # Validation entry type
  if [ "${ENTRY_TYPE}" == "" ]; then
      echo -e "${YELLOW}ENTRY_TYPE env must be provided${NC}"
      exit 1
  fi

  # Validation entry ttl
  if [ "${ENTRY_TTL}" == "" ]; then
    ENTRY_TTL="60"
  fi

  # Validation entry value
  if [ "${ENTRY_VALUE}" == "" ]; then
    ENTRY_VALUE="10.10.10.10"
  fi

  # Validation entry domain
  if [ "${ENTRY_DOMAIN}" == "" ]; then
    ENTRY_DOMAIN="example.com"
  fi

  # Validation entry rg
  if [ "${ENTRY_RG}" == "" ]; then
    ENTRY_RG="example-rg"
  fi

  # Validation entry vm_name
  if [ "${VM_NAME}" == "" ]; then
    VM_NAME="docker-node-01"
  fi


  # Setting entry values - if dns.yaml is overwritten by volume this simply doesn't take any effect. (it's a feature not a bug)
  sed -i -E "s|@@NAME@@|${ENTRY_NAME}|g" /var/www/app/config/dns.yaml
  sed -i -E "s|@@TYPE@@|${ENTRY_TYPE}|g" /var/www/app/config/dns.yaml
  sed -i -E "s|@@TTL@@|${ENTRY_TTL}|g" /var/www/app/config/dns.yaml
  sed -i -E "s|@@VALUE@@|${ENTRY_VALUE}|g" /var/www/app/config/dns.yaml
  sed -i -E "s|@@DOMAIN@@|${ENTRY_DOMAIN}|g" /var/www/app/config/dns.yaml
  sed -i -E "s|@@RG@@|${ENTRY_RG}|g" /var/www/app/config/dns.yaml
  sed -i -E "s|@@VM_NAME@@|${VM_NAME}|g" /var/www/app/config/dns.yaml
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

# Run dns azure
python /var/www/app/app/main.py