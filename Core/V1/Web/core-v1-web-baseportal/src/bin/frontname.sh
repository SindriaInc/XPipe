#!/usr/bin/env bash

#set -e

# Setting Colors
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
NC='\033[0m' #No Color
NOW=$(date "+%Y-%m-%d_%H-%M-%S")

# Sample: admin_1id3jw

echo -e "${BLUE}Matching generated backend frontname...${NC}"
VALUE=$(grep -i -E '(admin_)' /var/www/app/app/etc/env.php | cut -d '>' -f 2 | cut -d "'" -f 2)
echo $VALUE
echo

echo -e "${BLUE}Setting dashboard backend frontname...${NC}"
sed -i -E "s|$VALUE|dashboard|g" /var/www/app/app/etc/env.php
echo

echo -e "${BLUE}Done.${NC}"
