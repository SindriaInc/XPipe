#!/usr/bin/env bash

#set -e

# Setting Colors
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
NC='\033[0m' #No Color
NOW=$(date "+%Y-%m-%d_%H-%M-%S")

CONTAINER_NAME=nanoREST
NANOREST_VERSION=2.2.0

echo -e "${BLUE}Building and Packaging...${NC}"
docker exec -t ${CONTAINER_NAME} bash -c "mvn compile; mvn package"
echo

echo -e "${BLUE}Deploy local...${NC}"
cp /Users/lucapitzoi/XPipe/Lib/nanoREST/src/target/nanoREST-${NANOREST_VERSION}.jar /Users/lucapitzoi/XPipe/Lib/blog/src/libs/nanoREST-${NANOREST_VERSION}.jar
echo

echo -e "${BLUE}Deploy artifact...${NC}"
echo


echo -e "${BLUE}Done.${NC}"
