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

# Listing /Modules
ls -la ../../Modules/

# Copy Core Modules into app/code/Core
cp -R ../../Core/V1/Module/* src/app/code/Core/

# Listing app/code/Core
ls -la src/app/code/Core/

# Build and publish only amd64 without manifest
bash build.sh ${IMAGE_NAME} ${TAG_VERSION} amd64
docker tag ${IMAGE_NAME}:${TAG_VERSION}-amd64 ${IMAGE_NAME}:${TAG_VERSION}
docker push ${IMAGE_NAME}:${TAG_VERSION}
docker push ${IMAGE_NAME}:latest