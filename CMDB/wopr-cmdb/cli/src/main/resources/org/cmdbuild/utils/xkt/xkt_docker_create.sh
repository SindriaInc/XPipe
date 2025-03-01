#!/bin/bash

dockerimagename="cmdbuild-xeokit:#cmdbuildversion#"
dockerfile="$1"

echo "Creating $dockerimagename docker image"

docker build -t $dockerimagename $dockerfile

docker tag cmdbuild-xeokit:#cmdbuildversion# cmdbuild-xeokit:latest

echo "docker image created"
