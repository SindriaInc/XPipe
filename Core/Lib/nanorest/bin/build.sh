#!/usr/bin/env bash

#set -e

# Setting Colors
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
NC='\033[0m' #No Color
NOW=$(date "+%Y-%m-%d_%H-%M-%S")

CONTAINER_NAME=nanorest
NANOREST_VERSION=1.0.0

echo -e "${BLUE}Building and Packaging...${NC}"
docker exec -t ${CONTAINER_NAME} bash -c "mvn compile; mvn package"
echo

echo -e "${BLUE}Deploy local...${NC}"
cp $HOME/XPipe/Core/Lib/nanorest/target/nanorest-${NANOREST_VERSION}.jar $HOME/XPipe/Core/Lib/blog/libs/nanorest-${NANOREST_VERSION}.jar
cp $HOME/XPipe/Core/Lib/nanorest/target/nanorest-${NANOREST_VERSION}.jar $HOME/XPipe/Fnd/V1/Rest/fnd-v1-rst-notifications/libs/nanorest-${NANOREST_VERSION}.jar
echo

echo -e "${BLUE}Deploy artifact...${NC}"
echo


echo -e "${BLUE}Done.${NC}"
