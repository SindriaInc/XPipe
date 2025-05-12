#!/usr/bin/env bash

if [[ -z "$1" ]]; then
    echo "Provide image name as first argument (eg. sindriainc/<repo-slug>)"
    exit 1
fi

if [[ -z "$2" ]]; then
    echo "Provide a tag version as second argument (eg. 1.0.0)"
    exit 1
fi

#if [[ -z "$3" ]]; then
#    echo "Provide a tag arch as third argument (eg. amd64, arm64)"
#    exit 1
#fi

IMAGE_NAME=$1
TAG_VERSION=$2
#TAG_ARCH=$3

XDEV_DISPLAY=:1

docker build ./src \
    --tag ${IMAGE_NAME}:${TAG_VERSION} \
    --tag ${IMAGE_NAME}:latest \
    --build-arg TAG_VERSION=${TAG_VERSION} \
    --build-arg XDEV_DISPLAY=${XDEV_DISPLAY}

#docker build ./src \
#    --tag ${IMAGE_NAME}:${TAG_VERSION} \
#    --tag ${IMAGE_NAME}:latest \
#    --build-arg ARCH=${TAG_ARCH} \
#    --build-arg TAG_VERSION=${TAG_VERSION} \
#    --build-arg XDEV_DISPLAY=${XDEV_DISPLAY}