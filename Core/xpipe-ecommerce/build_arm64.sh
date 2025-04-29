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

# Copy Modules into app/code/Sindria
cp -R ../../Modules/* src/app/code/Sindria/

# Listing app/code/Sindria
ls -la src/app/code/Sindria/

# arm64
## Disabled temp
##bash build.sh ${IMAGE_NAME} ${TAG_VERSION} arm64v8
# Workaround with wrong arch
bash build.sh ${IMAGE_NAME} ${TAG_VERSION} amd64
docker tag ${IMAGE_NAME}:${TAG_VERSION}-amd64 ${IMAGE_NAME}:${TAG_VERSION}-arm64v8