#!/usr/bin/env bash

#set -e

# Setting Colors
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
NC='\033[0m' #No Color
NOW=$(date "+%Y-%m-%d_%H-%M-%S")

#echo -e "${BLUE}Debug settings.xml...${NC}"
#cat /usr/share/java/maven-3/conf/settings.xml
#echo

echo -e "${BLUE}Cloning source code...${NC}"
BRANCH=$(cat /staging/branch.txt)
echo ${BRANCH}
git clone --branch ${BRANCH} ${IAC_GIT_PROTOCOL}${IAC_GIT_USERNAME}:${IAC_GIT_ACCESS_TOKEN}@${IAC_GIT_PROVIDER}/${IAC_GIT_NAMESPACE}/_git/${REPO_SLUG} /context
git checkout ${RELEASE_VERSION}
echo

echo -e "${BLUE}Setting permissions...${NC}"
chmod -R 775 /context
chown -R ${SINDRIA_USER}:${SINDRIA_USER} /context
echo

echo -e "${BLUE}Checking if JGitVer is present...${NC}"
if [ -e /context/.mvn ]; then
  echo -e "${BLUE}JGitVer already present, skip.${NC}"
else
  echo -e "${YELLOW}JGitVer isn't present, patching...${NC}"
  mkdir -p /context/.mvn
  cp /var/www/app/resources/jgitver/extensions.xml /context/.mvn/extensions.xml
  cp /var/www/app/resources/jgitver/jgitver.config.xml /context/.mvn/jgitver.config.xml
fi
echo

echo -e "${BLUE}Setting permissions...${NC}"
chmod -R 775 /context
chown -R ${SINDRIA_USER}:${SINDRIA_USER} /context
echo

#echo -e "${BLUE}Running mvn validate...${NC}"
#mvn validate > validate.txt
#cat validate.txt | tail -30
#echo -e "${BLUE}Ok${NC}"
#echo

echo -e "${BLUE}Extracting dependencies tree...${NC}"
#mvn dependency:tree > tree.txt
touch tree.txt
#cat tree.txt
echo -e "${BLUE}Ok${NC}"
echo

echo -e "${BLUE}Extracting project version...${NC}"
echo ${RELEASE_VERSION} > version.txt
#mvn help:evaluate -Dexpression=project.version | grep -e '^[^\[]' > dirt_version.txt
#cat dirt_version.txt | tail -1 > version.txt
cat version.txt
echo -e "${BLUE}Ok${NC}"
echo

echo -e "${BLUE}Exporting artifacts...${NC}"
cp tree.txt /staging
cp version.txt /staging
echo -e "${BLUE}Ok${NC}"
echo

echo -e "${BLUE}Done.${NC}"