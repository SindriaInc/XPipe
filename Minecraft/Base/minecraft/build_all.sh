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

for MINECRAFT_VERSION in 1.8.9 1.20.6 1.21 1.21.3 1.21.7
do
  # amd64
	bash build.sh ${IMAGE_NAME} ${TAG_VERSION} ${MINECRAFT_VERSION} amd64
  docker push ${IMAGE_NAME}:${TAG_VERSION}-${MINECRAFT_VERSION}-amd64
  # arm64
	bash build.sh ${IMAGE_NAME} ${TAG_VERSION} ${MINECRAFT_VERSION} arm64v8
  docker push ${IMAGE_NAME}:${TAG_VERSION}-${MINECRAFT_VERSION}-arm64v8

  # manifest
  docker manifest create ${IMAGE_NAME}:${TAG_VERSION}-${MINECRAFT_VERSION} --amend ${IMAGE_NAME}:${TAG_VERSION}-${MINECRAFT_VERSION}-amd64 --amend ${IMAGE_NAME}:${TAG_VERSION}-${MINECRAFT_VERSION}-arm64v8
  docker manifest push ${IMAGE_NAME}:${TAG_VERSION}-${MINECRAFT_VERSION}
done
