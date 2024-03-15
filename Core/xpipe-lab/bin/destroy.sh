#!/usr/bin/env bash

docker stop xpipe-xdev-lab-control-plane
docker stop xpipe-xdev-lab-worker

docker rm xpipe-xdev-lab-control-plane
docker rm xpipe-xdev-lab-worker