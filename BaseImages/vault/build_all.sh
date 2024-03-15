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

BASE_IMAGE=hashicorp/vault

for VAULT_VERSION in 1.12.0 1.12 1.13.1 1.13
do

  BASE_MANIFEST_DIRTY="[$(docker manifest inspect ${BASE_IMAGE}:${VAULT_VERSION} -v | jq -c '.[]' | jq -r '.Descriptor | select(.platform.architecture|test("arm64|amd64"))' | awk '{printf("%s",$0)} END { printf "\n" }')"]
  BASE_MANIFEST_LIST=$(echo $BASE_MANIFEST_DIRTY | sed -e "s|}}{|}},{|g")

  BASE_DIGEST_AMD64=$(echo ${BASE_MANIFEST_LIST} | jq -c '.[0]' | jq -r '.digest')
  BASE_DIGEST_ARM64=$(echo ${BASE_MANIFEST_LIST} | jq -c '.[1]' | jq -r '.digest')

  # amd64
	bash build.sh ${IMAGE_NAME} ${TAG_VERSION} ${VAULT_VERSION} amd64 linux/amd64 ${BASE_DIGEST_AMD64}
  docker push ${IMAGE_NAME}:${TAG_VERSION}-${VAULT_VERSION}-amd64
  # arm64
	bash build.sh ${IMAGE_NAME} ${TAG_VERSION} ${VAULT_VERSION} arm64v8 linux/arm64 ${BASE_DIGEST_ARM64}
  docker push ${IMAGE_NAME}:${TAG_VERSION}-${VAULT_VERSION}-arm64v8

  # manifest
  docker manifest create ${IMAGE_NAME}:${TAG_VERSION}-${VAULT_VERSION} --amend ${IMAGE_NAME}:${TAG_VERSION}-${VAULT_VERSION}-amd64 --amend ${IMAGE_NAME}:${TAG_VERSION}-${VAULT_VERSION}-arm64v8
  docker manifest push ${IMAGE_NAME}:${TAG_VERSION}-${VAULT_VERSION}
done