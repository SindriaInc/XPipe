#!/usr/bin/env bash

source .env

# shellcheck disable=SC2046
docker run --rm -t --env AWS_ACCESS_KEY_ID="${AWS_ACCESS_KEY_ID}" --env AWS_SECRET_ACCESS_KEY="${AWS_SECRET_ACCESS_KEY}" --env AWS_DEFAULT_REGION="${AWS_DEFAULT_REGION}" --env BACKUP_BUCKET_NAME="${BACKUP_BUCKET_NAME}" --env DB_HOST="${DB_HOST}" --env DB_USERNAME="${DB_USERNAME}" --env DB_PORT="${DB_PORT}" --env DB_NAME="${DB_NAME}" --env APP_NAME="${APP_NAME}" --env DB_PASSWORD="$(echo $(sed -e 's/\$/\\$/g' <<< $DB_PASSWORD))" sindriainc/postgres-backup:1.0.1
