#!/usr/bin/env bash

set -e

BLUE='\033[0;34m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
NC='\033[0m' #No Color
NOW=$(date "+%Y-%m-%d_%H-%M-%S")

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
echo -e "${BLUE}Creating backup...${NC}"

# Dump scheme
pg_dump -U ${DB_USERNAME} -h ${DB_HOST} -p ${DB_PORT} -d ${DB_NAME} -w -f ${APP_NAME}_${NOW}.sql

# Adding latest tag for easy restore
cp *.sql ${APP_NAME}_latest.sql

echo #
echo -e "${BLUE}Uploading backup...${NC}"

# Init for upload dump
mkdir -p tmp
mv *.sql tmp/

# Uploading dump
aws s3 sync ./tmp s3://${BACKUP_BUCKET_NAME}

echo #
echo -e "${BLUE}Done.${NC}"