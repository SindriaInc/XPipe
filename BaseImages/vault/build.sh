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
    echo "Provide a tag suffix as third argument (eg. 1.12.0)"
    exit 1
fi

if [[ -z "$4" ]]; then
    echo "Provide a tag arch as fourth argument (eg. amd64, arm64)"
    exit 1
fi

if [[ -z "$5" ]]; then
    echo "Provide a tag platform as fifth argument (eg. linux/amd64, linux/arm64/v8)"
    exit 1
fi

if [[ -z "$6" ]]; then
    echo "Provide a tag digest as sixth argument (eg. sha256:09354ca0891f7cee8fbfe8db08c62d2d757fad8ae6c91f2b6cce7a34440e3fae)"
    exit 1
fi

IMAGE_NAME=$1
TAG_VERSION=$2
TAG_SUFFIX=$3
TAG_ARCH=$4
TAG_PLATFORM=$5
TAG_DIGEST=$6

HOST_USER_UID=1000
TIMEZONE=Europe/Rome

docker build --tag ${IMAGE_NAME}:${TAG_VERSION}-${TAG_SUFFIX}-${TAG_ARCH} --build-arg PLATFORM=${TAG_PLATFORM} --build-arg DIGEST=${TAG_DIGEST} --build-arg ARCH=${TAG_ARCH} --build-arg TAG_VERSION=${TAG_VERSION} --build-arg TAG_SUFFIX=${TAG_SUFFIX} --build-arg HOST_USER_UID=${HOST_USER_UID} --build-arg TIMEZONE=${TIMEZONE} -<<EOF
ARG PLATFORM
ARG DIGEST
ARG ARCH
ARG TAG_SUFFIX
FROM --platform=\$PLATFORM hashicorp/vault@\$DIGEST

ARG TAG_VERSION
ARG TAG_SUFFIX
ARG HOST_USER_UID
ARG TIMEZONE

LABEL org.opencontainers.image.authors="Sindria Inc. <info@sindria.org>"

LABEL \
	name="vault" \
	image="sindriainc/vault" \
	tag="\${TAG_VERSION}-\${TAG_SUFFIX}" \
	vendor="sindria"

ENV TZ=\${TIMEZONE}
ENV SINDRIA_USER="sindria"
ENV SINDRIA_USER_HOME="/home/sindria"

# Setting timezone
RUN ln -snf /usr/share/zoneinfo/\$TZ /etc/localtime && echo \$TZ > /etc/timezone
EOF