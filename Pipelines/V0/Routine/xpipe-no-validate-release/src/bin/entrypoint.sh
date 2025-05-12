#!/usr/bin/env bash

#set -e

# Setting Colors
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
NC='\033[0m' #No Color
NOW=$(date "+%Y-%m-%d_%H-%M-%S")


#PATTERN_TAG='[0-9]+\.[0-9]+\.[0-9]+-[a-z]+'
PATTERN_TAG='^([0-9]+\.[0-9]+\.[0-9]+)(-)([a-z]+)$'
PATTERN_RELEASE='[0-9]+\.[0-9]+\.[0-9]+'
PATTERN_BRANCH='[a-z]+'
PATTERN_ALPHA='^([0-9]+\.[0-9]+\.[0-9]+)(-)(alpha\.)([0-9]+)$'

TAG=None
RELEASE=None
BRANCH=None

echo -e "${BLUE}Validating RELEASE_VERSION...${NC}"

echo -e "${BLUE}Input: RELEASE_VERSION${NC}"
echo ${RELEASE_VERSION}
echo

# Filter only accepted pattern
if [[ ! ${RELEASE_VERSION} =~ ${PATTERN_TAG} ]]; then
  echo -e "${YELLOW}RELEASE_VERSION not dev or test environment release${NC}"
  echo
  echo -e "${BLUE}Checking if RELEASE_VERSION is a production environment release... ${NC}"

  if [[ ! ${RELEASE_VERSION} =~ ${PATTERN_RELEASE} ]]; then
    echo -e "${RED}Fatal RELEASE_VERSION ${RELEASE_VERSION} provided not compliance${NC}"
    echo -e "${YELLOW}Please provide valid RELEASE_VERSION${NC}"
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

  if [[ ${RELEASE_VERSION} =~ ^[a-z][0-9]+\.[0-9]+\.[0-9]+$ ]]; then
    echo -e "${RED}Fatal RELEASE_VERSION ${RELEASE_VERSION} provided not compliance${NC}"
    echo -e "${YELLOW}Please provide valid RELEASE_VERSION${NC}"
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

  if [[ ${RELEASE_VERSION} =~ ^[0-9]+\.[0-9]+\.[0-9]+[a-z]$ ]]; then
    echo -e "${RED}Fatal RELEASE_VERSION ${RELEASE_VERSION} provided not compliance${NC}"
    echo -e "${YELLOW}Please provide valid RELEASE_VERSION${NC}"
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

fi
echo -e "${BLUE}Filter Ok, skip${NC}"

# Check if pattern release isn't present in RELEASE_VERSION provided (1.0.0)
if [[ ! ${RELEASE_VERSION} =~ ${PATTERN_RELEASE} ]]; then
  echo -e "${RED}Fatal RELEASE_VERSION ${RELEASE_VERSION} provided not compliance${NC}"
  echo -e "${YELLOW}Please provide valid RELEASE_VERSION${NC}"
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

# Check if pattern alpha match in RELEASE_VERSION provided (1.0.0-alpha.0)
#if [[ ${RELEASE_VERSION} =~ ${PATTERN_ALPHA} ]]; then
#  echo -e "${YELLOW}Warning: alpha tag version matched, this should be used only in libui or ui role component${NC}"
#  echo
#  echo -e "${BLUE}Setting branch...${NC}"
#  BRANCH="master"
#  echo "ok, skip"
#fi

# Check if pattern tag is present in RELEASE_VERSION provided (1.0.0-dev or 1.0.0-tst or 1.0.0-crt)
if [[ ${RELEASE_VERSION} =~ ${PATTERN_TAG} ]]; then
  BRANCH=$(echo ${RELEASE_VERSION} | grep -o -E ${PATTERN_BRANCH})

  if [ ${BRANCH} == "dev" ]; then
    echo "ok, skip"
  elif [ ${BRANCH} == "tst" ]; then
    echo "ok, skip"
  elif [ ${BRANCH} == "crt" ]; then
    echo "ok, skip"
  else
    echo -e "${RED}Fatal RELEASE_VERSION ${RELEASE_VERSION} provided not compliance${NC}"
    echo -e "${YELLOW}Please provide valid RELEASE_VERSION${NC}"
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

fi

echo

echo "Match Tag:"
if [[ ! ${RELEASE_VERSION} =~ ${PATTERN_TAG} ]]; then
  echo "None" > match.log
  cat match.log | tail -1
else
  echo ${RELEASE_VERSION} | grep -o -E ${PATTERN_TAG} > match.log
  TAG=$(cat match.log | tail -1)
  cat match.log | tail -1
fi
echo

echo "Match Release:"
if [[ ! ${RELEASE_VERSION} =~ ${PATTERN_RELEASE} ]]; then
  echo "None" >> match.log
  cat match.log | tail -1
else
  echo ${RELEASE_VERSION} | grep -o -E ${PATTERN_RELEASE} >> match.log
  RELEASE=$(cat match.log | tail -1)
  cat match.log | tail -1
fi
echo

echo "Match Branch:"
if [[ ! ${RELEASE_VERSION} =~ ${PATTERN_BRANCH} ]]; then
  echo "None" >> match.log
  cat match.log | tail -1
else
  echo ${RELEASE_VERSION} | grep -o -E ${PATTERN_BRANCH} >> match.log
  #BRANCH=$(cat match.log | tail -1)
  cat match.log | tail -1
fi
echo

echo "Result:"
cat match.log
echo

echo -e "${BLUE}Checking branch matched...${NC}"
echo "Branch: ${BRANCH}"

if [[ "${BRANCH}" == "" ]]; then
  echo -e "${YELLOW}Warning void branch matched!${NC}"
  echo
  echo -e "${BLUE}Fixing branch matched...${NC}"
  BRANCH="master"
fi

if [[ "${BRANCH}" == "None" ]]; then
  echo -e "${YELLOW}Warning None branch matched!${NC}"
  echo
  echo -e "${BLUE}Fixing branch matched...${NC}"
  BRANCH="master"
fi

echo

echo -e "${BLUE}Results values:${NC}"
echo
echo -e "${BLUE}TAG: ${TAG}${NC}"
echo -e "${BLUE}RELEASE: ${RELEASE}${NC}"
echo -e "${BLUE}BRANCH: ${BRANCH}${NC}"
echo

echo -e "${BLUE}Generating artifacts...${NC}"
echo ${TAG} > tag.txt
echo ${RELEASE} > release.txt
echo ${BRANCH} > branch.txt
echo

echo -e "${BLUE}Exporting artifacts...${NC}"
cp match.log /staging
cp tag.txt /staging
cp release.txt /staging
cp branch.txt /staging
echo

echo -e "${BLUE}Done.${NC}"