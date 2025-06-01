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

IMAGE_NAME=$1
TAG_VERSION=$2
TAG_ARCH=$3

HOST_USER_UID=1000
TIMEZONE=Europe/Rome
XDEV_SINDRIA_USER_PASSWORD=sindria
XDEV_SINDRIA_USER_PUBKEY="ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABgQDBjVW2tS/yVwcvPKwfd2jyowzsBUAI73+CY9JNP175LPjQWuAuCv90HU7QKuKIwECELt6h1nO4if3u7LFsUnkRK/OMn2lbXu6ypufDHDmLTF4GyXhWK9sFK5/gePbHk+M7egDDzCd1Ww75YR9IzBHSsyQWi9LnAoBAUNe5Av6MgSRSI+4pHzL3TLdzLmFJ8AbL0rX21Hsw4WNsACUGwgaP7EuHXGqo8RI0g9K9MM7Aq9TxLjxo7fwfXxYAqqJcSnRQjN1lUnkRVtdkeEqQ9C7cptl70kKGi4BrwB8bf7BmmN7YUwTNwFTrgSB8xuPx1mSN4TFsPLUL3GZDj7PpddRH7MTF5GRoSAg8FEF7CgL9TZxxbN+Ea2y4SJgzPNGvrq0HZCzxCk4st+cBdfCiopgBPtCBVDOPLYEMf52ltMcqGNWQESTdBZ4nIQkkEe9t6DCBQBU3v0reHNyTU8BdTNCii0ecBtBPt0z6g3+sR/vWe+DegnkpeBwpCDS9Y/ZOu08= sindria@xdev.local"
XDEV_DISPLAY=:1

#docker build ./src \
#    --tag ${IMAGE_NAME}:${TAG_VERSION} \
#    --tag ${IMAGE_NAME}:latest \
#    --build-arg TAG_VERSION=${TAG_VERSION} \
#    --build-arg HOST_USER_UID=${HOST_USER_UID} \
#    --build-arg XDEV_SINDRIA_USER_PASSWORD=${XDEV_SINDRIA_USER_PASSWORD} \
#    --build-arg XDEV_SINDRIA_USER_PUBKEY="${XDEV_SINDRIA_USER_PUBKEY}" \
#    --build-arg XDEV_DISPLAY=${XDEV_DISPLAY} \
#    --build-arg TIMEZONE=${TIMEZONE}

docker build ./src \
    --tag ${IMAGE_NAME}:${TAG_VERSION} \
    --tag ${IMAGE_NAME}:latest \
    --build-arg ARCH=${TAG_ARCH} \
    --build-arg TAG_VERSION=${TAG_VERSION} \
    --build-arg HOST_USER_UID=${HOST_USER_UID} \
    --build-arg XDEV_SINDRIA_USER_PASSWORD=${XDEV_SINDRIA_USER_PASSWORD} \
    --build-arg XDEV_SINDRIA_USER_PUBKEY="${XDEV_SINDRIA_USER_PUBKEY}" \
    --build-arg XDEV_DISPLAY=${XDEV_DISPLAY} \
    --build-arg TIMEZONE=${TIMEZONE}