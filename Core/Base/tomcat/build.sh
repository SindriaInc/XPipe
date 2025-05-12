#!/usr/bin/env bash

if [[ -z "$1" ]]; then
    echo "Provide image name as first argument (eg. sindriainc/<repo-slug>)"
    exit 1
fi

if [[ -z "$2" ]]; then
    echo "Provide a tag version as second argument (eg. 1.0.0)"
    exit 1
fi

if [[ -z "$3" ]]; then
    echo "Provide a tag suffix as third argument (eg. 8.5.33)"
    exit 1
fi

if [[ -z "$4" ]]; then
    echo "Provide a java version as third argument (eg. 8)"
    exit 1
fi

if [[ -z "$5" ]]; then
    echo "Provide a tag arch as fourth argument (eg. amd64, arm64)"
    exit 1
fi

IMAGE_NAME=$1
TAG_VERSION=$2
TAG_SUFFIX=$3
JAVA_VERSION=$4
TAG_ARCH=$5

HOST_USER_UID=1000
TIMEZONE=Europe/Rome

# Only for test build
if [ "${TAG_VERSION}" == "test" ]; then
    docker build ./test/src \
    --tag ${IMAGE_NAME}:${TAG_VERSION}-${TAG_SUFFIX}-${JAVA_VERSION}-${TAG_ARCH} \
    --tag ${IMAGE_NAME}:latest \
    --build-arg ARCH=${TAG_ARCH} \
    --build-arg JAVA_VERSION=${JAVA_VERSION} \
    --build-arg TAG_VERSION=${TAG_VERSION} \
    --build-arg TAG_SUFFIX=${TAG_SUFFIX} \
    --build-arg HOST_USER_UID=${HOST_USER_UID} \
    --build-arg TIMEZONE=${TIMEZONE}
fi

if [ "${TAG_VERSION}" != "test" ]; then
    docker build ./src \
    --tag ${IMAGE_NAME}:${TAG_VERSION}-${TAG_SUFFIX}-${JAVA_VERSION}-${TAG_ARCH} \
    --tag ${IMAGE_NAME}:latest \
    --build-arg ARCH=${TAG_ARCH} \
    --build-arg JAVA_VERSION=${JAVA_VERSION} \
    --build-arg TAG_VERSION=${TAG_VERSION} \
    --build-arg TAG_SUFFIX=${TAG_SUFFIX} \
    --build-arg HOST_USER_UID=${HOST_USER_UID} \
    --build-arg TIMEZONE=${TIMEZONE}
fi