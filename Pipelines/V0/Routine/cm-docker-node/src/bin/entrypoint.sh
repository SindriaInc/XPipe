#!/usr/bin/env bash

set -e

# Setting Colors
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
NC='\033[0m' #No Color
NOW=$(date "+%Y-%m-%d_%H-%M-%S")

# Validation ssh private key env
if [ "${IAC_PRIVATE_KEY}" == "" ]; then
    echo -e "${YELLOW}IAC_PRIVATE_KEY env must be provided${NC}"
    exit 1
fi

# Setup ssh private key
mkdir -p /root/.ssh
(umask 077; echo ${IAC_PRIVATE_KEY} | base64 -d > /root/.ssh/sindria@cm)
chmod 700 /root/.ssh
chmod 600 /root/.ssh/*


# Validation inventory name env
if [ "${IAC_INVENTORY_NAME}" == "" ]; then
    echo -e "${YELLOW}IAC_INVENTORY_NAME env must be provided${NC}"
    exit 1
fi

# Validation remote user env
if [ "${IAC_REMOTE_USER}" == "" ]; then
    echo -e "${YELLOW}IAC_REMOTE_USER env must be provided${NC}"
    exit 1
fi

# Setting ansible configuration
sed -i -E "s|@@IAC_INVENTORY_NAME@@|${IAC_INVENTORY_NAME}|g" /var/www/app/ansible.cfg
sed -i -E "s|@@IAC_REMOTE_USER@@|${IAC_REMOTE_USER}|g" /var/www/app/ansible.cfg
sed -i -E "s|@@IAC_REMOTE_USER@@|${IAC_REMOTE_USER}|g" /var/www/app/main.yml


if [ "${IAC_MODE}" == "standalone" ]; then

  # Validation host
  if [ "${HOST}" == "" ]; then
      echo -e "${YELLOW}HOST env must be provided${NC}"
      exit 1
  fi

  # Setting hosts values - if hosts is overwritten by volume this simply doesn't take any effect. (it's a feature not a bug)
  sed -i -E "s|@@HOST@@|${HOST}|g" /var/www/app/inventory/hosts
else
  # Cleanup config directory
  rm -Rf /var/www/app/config
  mkdir -p /var/www/app/config

  # Infra setup
  mkdir -p .setup
  cd .setup
  git clone https://${IAC_GIT_USERNAME}:${IAC_GIT_PASSWORD}@${IAC_GIT_PROVIDER}/${IAC_GIT_NAMESPACE}/${IAC_INFRA_NAME}.git
  cp ${IAC_INFRA_NAME}/config/* ../config
  cd ..

  # Init inventory
  rm -Rf /var/www/app/inventory
  mkdir -p /var/www/app/inventory

  if [ "${IAC_INVENTORY_REMOTE}" == "git" ]; then
    # Inventory cache
    mkdir -p .cache
    cd .cache
    git clone https://${IAC_GIT_USERNAME}:${IAC_GIT_PASSWORD}@${IAC_GIT_PROVIDER}/${IAC_GIT_NAMESPACE}/${IAC_INVENTORY_CACHE}.git
    cp ${IAC_INVENTORY_CACHE}/* ../inventory
    cd ..
  elif [ "${IAC_INVENTORY_REMOTE}" == "s3" ]; then
    # Sync inventory - Dowload
    aws s3 sync s3://${IAC_INVENTORY_CACHE} ./inventory
  else
    echo -e "${RED}IAC_INVENTORY_REMOTE_TYPE env value unsupported, abort${NC}"
    exit 1
  fi
fi

# Dry run playboook
#ansible-playbook main.yml --check

# Run playbook
ansible-playbook main.yml
