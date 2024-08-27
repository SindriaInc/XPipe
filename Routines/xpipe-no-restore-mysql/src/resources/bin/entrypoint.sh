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

# Setup .my.cnf
#touch /root/.my.cnf

# Build placeholder string
#STRING="[mysqldump]\user=@@DB_USERNAME@@\password=@@DB_PASSWORD@@"

# Overwrite placeholder string
#echo "${STRING}" > /root/.my.cnf

# Overwrite unclean db password
echo ${DB_PASSWORD} > unclean.txt

# Cleanup db password colon and & operator
sed -i -e 's/:/\\\\:/g' unclean.txt
sed -i -e 's/&/\\&/g' unclean.txt

# Find and replace db password placeholder with real cleaned db password
#sed -i -E "s|@@DB_PASSWORD@@|$(cat unclean.txt)|g" /root/.my.cnf

# Find and replace db username placeholder
#sed -i -E "s|@@DB_USERNAME@@|${DB_USERNAME}|g" /root/.my.cnf

# Setting permission
#chmod 600 /root/.my.cnf

echo #
echo -e "${BLUE}Checking if schema exists...${NC}"

# Preparing sql files
printf "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '$RESTORE_SCHEMA';" > /var/www/app/check.sql
printf "CREATE DATABASE IF NOT EXISTS $RESTORE_SCHEMA;" > /var/www/app/create.sql

# Create schema if not exists
# CREATE DATABASE IF NOT EXISTS DBName;
mysql -h ${DB_HOST} -u ${DB_USERNAME} -p${DB_PASSWORD} < /var/www/app/create.sql

echo #
echo -e "${BLUE}Restoring backup...${NC}"

# Restore schema
#mysql -u user -p data_base_name_here < db.sql
mysql -h ${DB_HOST} -u ${DB_USERNAME} -p${DB_PASSWORD} ${RESTORE_SCHEMA} < restore/${APP_NAME}_${RESTORE_TAG}.sql

echo #
echo -e "${BLUE}Done.${NC}"