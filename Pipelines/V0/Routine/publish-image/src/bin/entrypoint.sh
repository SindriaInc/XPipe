#!/usr/bin/env bash

set -e

# Setting Colors
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
NC='\033[0m' #No Color
NOW=$(date "+%Y-%m-%d_%H-%M-%S")


if [ "${IAC_MODE}" == "standalone" ]; then

    # Checkout version
    echo -e "${BLUE}Checkout version...${NC}"
    git checkout ${IAC_APP_VERSION}

    # Sync certs if exists
    if [ -d certs ]; then
        rsync -ravP certs/ src/resources/nginx/certs/
    fi

    # Build Image
    if [ -f build.sh ]; then
        echo -e "${BLUE}Building image...${NC}"
        bash build.sh ${DOCKERHUB_NAMESPACE}/${IAC_APP_NAME} ${IAC_APP_VERSION}
    elif [ -f Makefile ]; then
        echo -e "${BLUE}Building image...${NC}"
        make
    else
      echo -e "${YELLOW}No valid build file found, abort.${NC}"
      exit 1
    fi

    # Login into registry
    echo -e "${BLUE}Login into registry...${NC}"
    echo ${DOCKERHUB_PASSWORD} | docker login --username "${DOCKERHUB_USERNAME}" --password-stdin

    # Push Image
    echo -e "${BLUE}Pushing image into registry...${NC}"
    docker push ${DOCKERHUB_NAMESPACE}/${IAC_APP_NAME}:${IAC_APP_VERSION}
    docker push ${DOCKERHUB_NAMESPACE}/${IAC_APP_NAME}:latest

    # Cleaning local registry
    echo -e "${BLUE}Cleaning local registry...${NC}"
    docker image rm ${DOCKERHUB_NAMESPACE}/${IAC_APP_NAME}:${IAC_APP_VERSION}
    docker image rm ${DOCKERHUB_NAMESPACE}/${IAC_APP_NAME}:latest

else
    # Init build workspace
    echo -e "${BLUE}Init build workspace...${NC}"
    mkdir -p .build
    cd .build

    # Cloning repo
    echo -e "${BLUE}Cloning repo...${NC}"
    git clone --branch="${IAC_APP_VERSION}" --depth 50 https://${IAC_GIT_USERNAME}:${IAC_GIT_PASSWORD}@${IAC_GIT_PROVIDER}/${IAC_GIT_NAMESPACE}/${IAC_APP_NAME}.git
    cd ${IAC_APP_NAME}

    # Sync certs if exists
    if [ -d certs ]; then
        rsync -ravP certs/ src/resources/nginx/certs/
    fi

    # Build Image
    if [ -f build.sh ]; then
        echo -e "${BLUE}Building image...${NC}"
        bash build.sh ${DOCKERHUB_NAMESPACE}/${IAC_APP_NAME} ${IAC_APP_VERSION}
    elif [ -f Makefile ]; then
        echo -e "${BLUE}Building image...${NC}"
        make
    else
      echo -e "${YELLOW}No valid build file found, abort.${NC}"
      exit 1
    fi

    # Login into registry
    echo -e "${BLUE}Login into registry...${NC}"
    echo ${DOCKERHUB_PASSWORD} | docker login --username "${DOCKERHUB_USERNAME}" --password-stdin

    # Push Image
    echo -e "${BLUE}Pushing image into registry...${NC}"
    docker push ${DOCKERHUB_NAMESPACE}/${IAC_APP_NAME}:${IAC_APP_VERSION}
    docker push ${DOCKERHUB_NAMESPACE}/${IAC_APP_NAME}:latest

    # Cleaning local registry
    echo -e "${BLUE}Cleaning local registry...${NC}"
    docker image rm ${DOCKERHUB_NAMESPACE}/${IAC_APP_NAME}:${IAC_APP_VERSION}
    docker image rm ${DOCKERHUB_NAMESPACE}/${IAC_APP_NAME}:latest
fi