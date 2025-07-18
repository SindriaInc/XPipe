#!/usr/bin/env bash

source .env

# shellcheck disable=SC2046
docker run --rm -t --env IAC_MODE=${IAC_MODE} --env AZURE_SUBSCRIPTION_ID=${AZURE_SUBSCRIPTION_ID} --env AZURE_CLIENT_ID=${AZURE_CLIENT_ID} --env AZURE_SECRET=${AZURE_SECRET} --env AZURE_TENANT=${AZURE_TENANT} --env AZURE_RESOURCE_GROUP=${AZURE_RESOURCE_GROUP} --env AZURE_STORAGE_ACCOUNT=${AZURE_STORAGE_ACCOUNT} --env AZURE_STORAGE_ACCESS_KEY=${AZURE_STORAGE_ACCESS_KEY} --env AZURE_STORAGE_CONNECTION_STRING=${AZURE_STORAGE_CONNECTION_STRING} --env AZURE_CONF=${AZURE_CONF} --env IAC_CERTBOT_CACHE=${IAC_CERTBOT_CACHE} --env IAC_CERTBOT_EMAIL=${IAC_CERTBOT_EMAIL} --env IAC_CERTBOT_DOMAIN=${IAC_CERTBOT_DOMAIN} sindriainc/maintenance-v1-rtn-certsazure:1.0.0