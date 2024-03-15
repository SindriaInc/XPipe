#!/usr/bin/env bash

set -e

# Setting Colors
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
NC='\033[0m' #No Color
NOW=$(date "+%Y-%m-%d_%H-%M-%S")


if [ "${IAC_MODE}" == "standalone" ]; then

  # Move into live
  # shellcheck disable=SC2164
  cd live

  # Init infra
  terraform init -input=false
  # Plan infra
  terraform plan -input=false
  # Apply infra
  terraform apply -input=false -auto-approve

  # Export infra data output
  terraform show -json > ../output.json

  # Move into root
  cd ..

  # Generate resources infra
  python3 infra.py

  # Export artifact
  cp infra.json current
else
  # Cleanup config directory
  rm -Rf /var/www/app/config
  mkdir -p /var/www/app/config

  # Infra setup
  mkdir -p .setup
  cd .setup
  git clone https://${IAC_GIT_USERNAME}:${IAC_GIT_PASSWORD}@${IAC_GIT_PROVIDER}/${IAC_GIT_NAMESPACE}/${IAC_INFRA_NAME}.git
  cp ${IAC_INFRA_NAME}/config/* ../config

  # Move into root
  cd ..

  # Create live cache
  mkdir -p .live-cache
  touch .live-cache/terraform.tfstate
  touch .live-cache/terraform.tfstate.backup

  # Sync live cache - Download
  aws s3 sync s3://${IAC_LIVE_CACHE} .live-cache
  cp .live-cache/terraform.tfstate live || true
  cp .live-cache/terraform.tfstate.backup live || true

  # Move into live
  # shellcheck disable=SC2164
  cd live

  # Init infra
  terraform init -input=false
  # Plan infra
  terraform plan -input=false
  # Apply infra
  terraform apply -input=false -auto-approve

  # Move into root
  cd ..

  # Sync live cache - Upload
  aws s3 sync ./live s3://${IAC_LIVE_CACHE}

  # Move into live
  # shellcheck disable=SC2164
  cd live

  # Export infra data output
  terraform show -json > ../output.json

  # Move into root
  cd ..

  # Generate resources infra
  python3 infra.py

  # Export artifact
  cp infra.json current

  # Sync current infra - Upload
  aws s3 sync ./current s3://${IAC_CURRENT_INFRA}
fi