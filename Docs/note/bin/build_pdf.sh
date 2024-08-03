#!/usr/bin/env bash

set -e

# Setting Colors
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
NC='\033[0m' #No Color
NOW=$(date "+%Y-%m-%d_%H-%M-%S")

if [[ -z "$1" ]]; then
    echo -e "${YELLOW}Provide source filename as first argument (eg. main)${NC}"
    exit 1
fi

if [[ -z "$2" ]]; then
    echo -e "${YELLOW}Provide semantic version as second argument (eg. 1.0)${NC}"
    exit 1
fi

CONTAINER_NAME=note
SOURCE_NAME=$1
SEMVER=$2
FILENAME=${SOURCE_NAME}_v${SEMVER}.pdf
BASE_PATH=/Users/lucapitzoi

docker exec -it ${CONTAINER_NAME} pandoc ${SOURCE_NAME}.md -o out.pdf
#docker exec -it ${CONTAINER_NAME} pandoc -f markdown-implicit_figures ${SOURCE_NAME}.md -o out.pdf
docker exec -it ${CONTAINER_NAME} mv /var/www/app/out.pdf /var/www/app/out/${FILENAME}

cp ${BASE_PATH}/Projects/Sindria/XPipe/Docs/note/src/out/${FILENAME} ${BASE_PATH}/Documents/Book/${FILENAME}