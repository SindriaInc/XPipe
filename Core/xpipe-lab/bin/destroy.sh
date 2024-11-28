#!/usr/bin/env bash

docker stop xpipe-lab-control-plane
docker stop xpipe-lab-worker

docker rm xpipe-lab-control-plane
docker rm xpipe-lab-worker