#!/usr/bin/env bash

if [[ -z "$1" ]]; then
    echo "Provide image name as first argument (eg. sindriainc/<repo-slug>)"
    exit 1
fi

if [[ -z "$2" ]]; then
    echo "Provide a tag version as second argument (eg. 1.0.0, local)"
    exit 1
fi

IMAGE_NAME=$1
TAG_VERSION=$2

for TOMCAT_VERSION in 8.0.53 8.5.93 9.0.80 10.1.13
do
  
  for JAVA_VERSION in 8 11 17
  do
    # amd64
    bash build.sh ${IMAGE_NAME} ${TAG_VERSION} ${TOMCAT_VERSION} ${JAVA_VERSION} amd64
    docker push ${IMAGE_NAME}:${TAG_VERSION}-${TOMCAT_VERSION}-${JAVA_VERSION}-amd64
    # arm64
    bash build.sh ${IMAGE_NAME} ${TAG_VERSION} ${TOMCAT_VERSION} ${JAVA_VERSION} arm64v8
    docker push ${IMAGE_NAME}:${TAG_VERSION}-${TOMCAT_VERSION}-${JAVA_VERSION}-arm64v8
  
    # manifest
    docker manifest create ${IMAGE_NAME}:${TAG_VERSION}-${TOMCAT_VERSION}-${JAVA_VERSION} --amend ${IMAGE_NAME}:${TAG_VERSION}-${TOMCAT_VERSION}-${JAVA_VERSION}-amd64 --amend ${IMAGE_NAME}:${TAG_VERSION}-${TOMCAT_VERSION}-${JAVA_VERSION}-arm64v8
    docker manifest push ${IMAGE_NAME}:${TAG_VERSION}-${TOMCAT_VERSION}-${JAVA_VERSION}
  done
  
done