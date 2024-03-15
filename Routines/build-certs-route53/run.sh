#!/usr/bin/env bash

source .env

# shellcheck disable=SC2046
docker run --rm -t --env IAC_MODE=${IAC_MODE} --env AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID} --env AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY} --env AWS_DEFAULT_REGION=${AWS_DEFAULT_REGION} --env IAC_CERTBOT_CACHE=${IAC_CERTBOT_CACHE} --env IAC_CERTBOT_EMAIL=${IAC_CERTBOT_EMAIL} --env IAC_CERTBOT_DOMAIN=${IAC_CERTBOT_DOMAIN} sindriainc/build-certs-route53:1.0.0