#!/usr/bin/env bash

if [[ -z "$1" ]]; then
    echo "Provide image name as first argument (eg. sindriaproject/<repo-slug>)"
    exit 1
fi

if [[ -z "$2" ]]; then
    echo "Provide a tag version as second argument (eg. 1.0.0)"
    exit 1
fi

if [[ -z "$3" ]]; then
    echo "Provide a tag arch as fourth argument (eg. amd64, arm64)"
    exit 1
fi


IMAGE_NAME=$1
TAG_VERSION=$2
TAG_ARCH=$3

HOST_USER_UID=1000
TIMEZONE=Europe/Rome

docker build ./src \
    --tag ${IMAGE_NAME}:${TAG_VERSION}-${TAG_ARCH} \
    --tag ${IMAGE_NAME}:latest \
    --build-arg ARCH=${TAG_ARCH} \
    --build-arg TAG_VERSION=${TAG_VERSION} \
    --build-arg HOST_USER_UID=${HOST_USER_UID} \
    --build-arg TIMEZONE=${TIMEZONE}