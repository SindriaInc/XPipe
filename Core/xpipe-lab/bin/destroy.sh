#!/usr/bin/env bash

set -e

# Setting Colors
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
NC='\033[0m' #No Color
NOW=$(date "+%Y-%m-%d_%H-%M-%S")

echo -e "${BLUE}Stopping services...${NC}"
docker stop xpipe-lab-control-plane
docker stop xpipe-lab-worker
echo

echo -e "${BLUE}Removing services...${NC}"
docker rm xpipe-lab-control-plane
docker rm xpipe-lab-worker
echo

echo -e "${BLUE}Done.${NC}"