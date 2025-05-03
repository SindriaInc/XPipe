#!/usr/bin/env bash

set -e

# Setting Colors
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
NC='\033[0m' #No Color
NOW=$(date "+%Y-%m-%d_%H-%M-%S")

echo -e "${BLUE}Input: DOCKERHUB_PRIVATE_NAMESPACE${NC}"
echo ${DOCKERHUB_PRIVATE_NAMESPACE}
echo

echo -e "${BLUE}Input: REPO_SLUG${NC}"
echo ${REPO_SLUG}
echo

echo -e "${BLUE}Input: RELEASE_VERSION${NC}"
echo ${RELEASE_VERSION}
echo


IMAGE="${DOCKERHUB_PRIVATE_NAMESPACE}/${REPO_SLUG}:${RELEASE_VERSION}"

echo -e "${BLUE}Validating OCI ${IMAGE}${NC}"

if docker manifest inspect "$IMAGE" > /dev/null 2>&1; then
  echo -e "${RED}Image already exists — blocking pipeline to avoid overwrite.${NC}"
  echo -e "${YELLOW}Please update your RELEASE_VERSION ${RELEASE_VERSION} using the correct semantic.${NC}"
  echo
  echo -e "${RED}Aborting entire pipeline...${NC}"
  touch /staging/pipeline.lock
  exit 1
else
  echo -e "${BLUE}Image not found — safe to build and push${NC}"
  exit 0
fi

echo -e "${BLUE}Done.${NC}"