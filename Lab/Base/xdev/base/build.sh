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
    echo "Provide a tag arch as third argument (eg. amd64, arm64)"
    exit 1
fi

if [[ -z "$4" ]]; then
    echo "Provide a platform arch as fourth argument (eg. linux/amd64, linux/arm64/v8)"
    exit 1
fi

if [[ -z "$5" ]]; then
    echo "Provide a kubectl arch as fifth argument (eg. amd64, arm64)"
    exit 1
fi

if [[ -z "$6" ]]; then
    echo "Provide a awscli arch as sixth argument (eg. x86_64, aarch64)"
    exit 1
fi

IMAGE_NAME=$1
TAG_VERSION=$2
TAG_ARCH=$3
PLATFORM=$4
KUBECTL_ARCH=$5
AWSCLI_ARCH=$6

HOST_USER_UID=1000
TIMEZONE=Europe/Rome
HOST_DOCKER_GROUP_UID=975
FEDORA_VERSION=42
XDEV_SINDRIA_USER_PASSWORD=sindria


# Only for test build
if [ "${TAG_VERSION}" == "test" ]; then
    docker build ./test/src \
    --tag ${IMAGE_NAME}:${TAG_VERSION}-${TAG_ARCH} \
    --tag ${IMAGE_NAME}:latest \
    --build-arg ARCH=${TAG_ARCH} \
    --build-arg PLATFORM=${PLATFORM} \
    --build-arg FEDORA_VERSION=${FEDORA_VERSION} \
    --build-arg TAG_VERSION=${TAG_VERSION} \
    --build-arg HOST_USER_UID=${HOST_USER_UID} \
    --build-arg HOST_DOCKER_GROUP_UID=${HOST_DOCKER_GROUP_UID} \
    --build-arg XDEV_SINDRIA_USER_PASSWORD=${XDEV_SINDRIA_USER_PASSWORD} \
    --build-arg KUBECTL_ARCH=${KUBECTL_ARCH} \
    --build-arg AWSCLI_ARCH=${AWSCLI_ARCH} \
    --build-arg TIMEZONE=${TIMEZONE}
fi

if [ "${TAG_VERSION}" != "test" ]; then
    docker build ./src \
    --tag ${IMAGE_NAME}:${TAG_VERSION}-${TAG_ARCH} \
    --tag ${IMAGE_NAME}:latest \
    --build-arg ARCH=${TAG_ARCH} \
    --build-arg PLATFORM=${PLATFORM} \
    --build-arg FEDORA_VERSION=${FEDORA_VERSION} \
    --build-arg TAG_VERSION=${TAG_VERSION} \
    --build-arg HOST_USER_UID=${HOST_USER_UID} \
    --build-arg HOST_DOCKER_GROUP_UID=${HOST_DOCKER_GROUP_UID} \
    --build-arg XDEV_SINDRIA_USER_PASSWORD=${XDEV_SINDRIA_USER_PASSWORD} \
    --build-arg KUBECTL_ARCH=${KUBECTL_ARCH} \
    --build-arg AWSCLI_ARCH=${AWSCLI_ARCH} \
    --build-arg TIMEZONE=${TIMEZONE}
fi