#!/usr/bin/env bash

if [[ -z "$1" ]]; then
    echo "Provide tag release as first argument (eg. 1.0.0-dev)"
    exit 1
fi

if [[ -z "$2" ]]; then
    echo "Provide a tag message as second argument (eg. \"Release 1.0.0-dev\")"
    exit 1
fi

TAG_RELEASE=$1
TAG_MESSAGE=$2

git tag -a ${TAG_RELEASE} -m "${TAG_MESSAGE}"
git push origin ${TAG_RELEASE}