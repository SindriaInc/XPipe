#!/usr/bin/env bash

TAG_RELEASE=$1

git tag -a ${TAG_RELEASE}
git push origin ${TAG_RELEASE}