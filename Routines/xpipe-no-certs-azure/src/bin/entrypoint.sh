#!/usr/bin/env bash

set -e

# Setting Colors
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
NC='\033[0m' #No Color
NOW=$(date "+%Y-%m-%d_%H-%M-%S")


if [ "${IAC_MODE}" == "standalone" ]; then

   # Init certbot cache
   echo -e "${BLUE}Init certbot cache...${NC}"
   mkdir -p /etc/letsencrypt

   # Azure ini
   echo -e "${BLUE}Setting azure ini...${NC}"
   (umask 077; echo ${AZURE_CONF} | base64 -d > azure.ini)
   chmod 600 azure.ini

   # Renew existing cert or build
   echo -e "${BLUE}Building certs...${NC}"
   #certbot certonly --authenticator dns-azure --preferred-challenges dns --noninteractive --agree-tos --dns-azure-config ~/.secrets/certbot/azure.ini -d example.com
   certbot renew -n --authenticator dns-azure --preferred-challenges dns --noninteractive --agree-tos --dns-azure-config azure.ini --dns-azure-propagation-seconds 60 --cert-name ${IAC_CERTBOT_DOMAIN} -m ${IAC_CERTBOT_EMAIL} || certbot certonly -n --authenticator dns-azure --preferred-challenges dns --noninteractive --agree-tos --dns-azure-config azure.ini --dns-azure-propagation-seconds 60 -d ${IAC_CERTBOT_DOMAIN} -d *.${IAC_CERTBOT_DOMAIN} -m ${IAC_CERTBOT_EMAIL}

else

  # Init certbot cache
  echo -e "${BLUE}Init certbot cache...${NC}"
  mkdir -p /etc/letsencrypt

  # Azure login
  az login --service-principal -u ${AZURE_CLIENT_ID} -p ${AZURE_SECRET} --tenant ${AZURE_TENANT}
  #az storage account keys list --resource-group ${AZURE_RESOURCE_GROUP} --account-name ${AZURE_STORAGE_ACCOUNT}
  #az storage account show-connection-string --name ${AZURE_STORAGE_ACCOUNT} --resource-group ${AZURE_RESOURCE_GROUP}

  #az storage blob list --container-name ${IAC_CERTBOT_CACHE}
  #az storage blob download-batch -d . -s ${IAC_CERTBOT_CACHE} --account-name ${AZURE_STORAGE_ACCOUNT} --account-key 00000000
  az storage blob download-batch -d /etc/letsencrypt -s ${IAC_CERTBOT_CACHE}

  # Cleanup symblinks
  echo -e "${BLUE}Cleanup symblinks...${NC}"
  rm -f /etc/letsencrypt/live/${IAC_CERTBOT_DOMAIN}/*.pem || true
  ln -s /etc/letsencrypt/archive/${IAC_CERTBOT_DOMAIN}/cert*.pem /etc/letsencrypt/live/${IAC_CERTBOT_DOMAIN}/cert.pem || true
  ln -s /etc/letsencrypt/archive/${IAC_CERTBOT_DOMAIN}/chain*.pem /etc/letsencrypt/live/${IAC_CERTBOT_DOMAIN}/chain.pem || true
  ln -s /etc/letsencrypt/archive/${IAC_CERTBOT_DOMAIN}/fullchain*.pem /etc/letsencrypt/live/${IAC_CERTBOT_DOMAIN}/fullchain.pem || true
  ln -s /etc/letsencrypt/archive/${IAC_CERTBOT_DOMAIN}/privkey*.pem /etc/letsencrypt/live/${IAC_CERTBOT_DOMAIN}/privkey.pem || true

  # Azure ini
  echo -e "${BLUE}Setting azure ini...${NC}"
  (umask 077; echo ${AZURE_CONF} | base64 -d > azure.ini)
  chmod 600 azure.ini

  # Renew existing cert or build
  echo -e "${BLUE}Building certs...${NC}"
  certbot renew -n --authenticator dns-azure --preferred-challenges dns --noninteractive --agree-tos --dns-azure-config azure.ini --dns-azure-propagation-seconds 60 --cert-name ${IAC_CERTBOT_DOMAIN} -m ${IAC_CERTBOT_EMAIL} || certbot certonly -n --authenticator dns-azure --preferred-challenges dns --noninteractive --agree-tos --dns-azure-config azure.ini --dns-azure-propagation-seconds 60 -d ${IAC_CERTBOT_DOMAIN} -d *.${IAC_CERTBOT_DOMAIN} -m ${IAC_CERTBOT_EMAIL}

  # Generating k8s tls secret
  echo -e "${BLUE}Generating k8s tls secret...${NC}"
  #NAME=${IAC_CERTBOT_DOMAIN//./_}
  NAME=${IAC_CERTBOT_DOMAIN//./}
  FULLNAME=tls-${NAME}
  echo $NAME
  echo
  cp /var/www/app/resources/tls-template.yaml /var/www/app/resources/${FULLNAME}.yaml
  sed -i -E "s|@@NAME@@|${FULLNAME}|g" /var/www/app/resources/${FULLNAME}.yaml
  cat /etc/letsencrypt/live/${IAC_CERTBOT_DOMAIN}/fullchain.pem | base64 > fullchain.base64
  cat /etc/letsencrypt/live/${IAC_CERTBOT_DOMAIN}/privkey.pem | base64 > privkey.base64
  cat fullchain.base64
  echo
  cat privkey.base64
  echo
  FULLCHAIN=$(tr -d '\n' < fullchain.base64)
  sed -i -E "s|@@FULLCHAIN@@|${FULLCHAIN}|g" /var/www/app/resources/${FULLNAME}.yaml
  PRIVKEY=$(tr -d '\n' < privkey.base64)
  sed -i -E "s|@@PRIVKEY@@|${PRIVKEY}|g" /var/www/app/resources/${FULLNAME}.yaml

  # Adding k8s resources to certbot cache
  echo -e "${BLUE}Adding k8s to certbot cache...${NC}"
  mkdir -p /etc/letsencrypt/k8s
  mkdir -p /etc/letsencrypt/k8s/${IAC_CERTBOT_DOMAIN}
  cp /var/www/app/resources/${FULLNAME}.yaml /etc/letsencrypt/k8s/${IAC_CERTBOT_DOMAIN}/${FULLNAME}.yaml
  echo

  # Update certbot cache
  echo -e "${BLUE}Updating certbot cache...${NC}"
  #az storage blob list --container-name ${IAC_CERTBOT_CACHE}
  #az storage blob sync -c mycontainer --account-name mystorageccount --account-key 00000000 -s "path/to/directory"
  #az storage blob sync -c ${IAC_CERTBOT_CACHE} --account-name ${AZURE_STORAGE_ACCOUNT} --account-key 00000000 -s "path/to/directory"
  az storage blob sync -s "/etc/letsencrypt" -c ${IAC_CERTBOT_CACHE}
fi