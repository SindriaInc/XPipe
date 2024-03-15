#!/bin/bash

dockerimagename="cmdbuild-xeokit:3.4.2"
dockerfile="$1"

echo "Creating $dockerimagename docker image"

docker build -t $dockerimagename $dockerfile

echo "docker image created"
