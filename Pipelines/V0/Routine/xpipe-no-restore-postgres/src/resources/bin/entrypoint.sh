#!/usr/bin/env bash

set -e

BLUE='\033[0;34m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
NC='\033[0m' #No Color
NOW=$(date "+%Y-%m-%d_%H-%M-%S")

# Setup restore schema name
if [ "${RESTORE_SCHEMA}" == "" ]; then
   BUILD_SCHEMA=restore_${APP_NAME}_${NOW}
   RESTORE_SCHEMA=$(echo "${BUILD_SCHEMA}" | tr '-' '_')
fi


echo -e "${BLUE}Fetching backups...${NC}"

# Init for download dumps
mkdir -p tmp

# Downloading dumps
aws s3 sync s3://${BACKUP_BUCKET_NAME} ./tmp

echo #
echo -e "${BLUE}Selecting backup to restore...${NC}"

# Init for select dump
mkdir -p restore

# Selecting dump to restore
cp tmp/${APP_NAME}_${RESTORE_TAG}.sql restore

echo #
echo -e "${BLUE}Setting up client...${NC}"

# Setup pgdump
touch /root/.pgpass

# Build placeholder string
STRING="*:*:*:"
STRING+=${DB_USERNAME}
STRING+=":"
STRING+="@@DB_PASSWORD@@"

# Overwrite placeholder string
echo "${STRING}" > /root/.pgpass

# Overwrite unclean db password
echo ${DB_PASSWORD} > unclean.txt

# Cleanup db password colon and & operator
sed -i -e 's/:/\\\\:/g' unclean.txt
sed -i -e 's/&/\\&/g' unclean.txt

# Find and replace db password placeholder with real cleaned db password
sed -i -E "s|@@DB_PASSWORD@@|$(cat unclean.txt)|g" /root/.pgpass

# Setting permission
chmod 600 /root/.pgpass

echo #
echo -e "${BLUE}Checking if schema exists...${NC}"

# Preparing sql files
printf "SELECT 1 FROM pg_database WHERE datname = '$RESTORE_SCHEMA';" > /var/www/app/check.sql
printf "CREATE DATABASE $RESTORE_SCHEMA WITH OWNER ${DB_USERNAME};" > /var/www/app/create.sql

# Create schema if not exists
#psql -U postgres -tc "SELECT 1 FROM pg_database WHERE datname = '<your db name>'" | grep -q 1 || psql -U postgres -c "CREATE DATABASE <your db name>"

psql -U ${DB_USERNAME} -h ${DB_HOST} -p ${DB_PORT} -d postgres -f /var/www/app/check.sql | grep -q 1 || echo -e "${YELLOW}Creating schema ${RESTORE_SCHEMA}...${NC}"; psql -U ${DB_USERNAME} -h ${DB_HOST} -p ${DB_PORT} -d postgres -f /var/www/app/create.sql


echo #
echo -e "${BLUE}Restoring backup...${NC}"

# Restore scheme
psql -U ${DB_USERNAME} -h ${DB_HOST} -p ${DB_PORT} -d ${RESTORE_SCHEMA} -f restore/${APP_NAME}_${RESTORE_TAG}.sql

echo #
echo -e "${BLUE}Done.${NC}"