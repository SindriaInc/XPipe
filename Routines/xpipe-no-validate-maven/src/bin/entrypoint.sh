#!/usr/bin/env bash

#set -e

# Setting Colors
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
NC='\033[0m' #No Color
NOW=$(date "+%Y-%m-%d_%H-%M-%S")

echo -e "${BLUE}Validating project version...${NC}"

PROJECT_VERSION=$(cat /staging/version.txt)

if [ "${RELEASE_VERSION}" == "${PROJECT_VERSION}" ]; then
    echo -e "${BLUE}Validation Ok, skip${NC}"
else
  echo -e "${RED}Fatal RELEASE_VERSION ${RELEASE_VERSION} provided is different with project.version ${PROJECT_VERSION}${NC}"
  echo -e "${YELLOW}Please provide the same RELEASE_VERSION and project.version${NC}"
  echo
  echo -e "${BLUE}Accepted formats:${NC}"
  echo -e "${BLUE}1.0.0-dev${NC}"
  echo -e "${BLUE}1.0.0-tst${NC}"
  echo -e "${BLUE}1.0.0-crt${NC}"
  echo -e "${BLUE}1.0.0${NC}"
  echo
  echo -e "${RED}Aborting entire pipeline...${NC}"
  touch /staging/pipeline.lock
  exit 1
fi

echo

echo -e "${BLUE}Done.${NC}"