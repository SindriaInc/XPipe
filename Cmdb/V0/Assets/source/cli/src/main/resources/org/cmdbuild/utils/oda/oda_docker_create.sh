#!/bin/bash

dockerimagename="cmdbuild-oda:#cmdbuildversion#"
dockerfile="$1"

echo "Creating $dockerimagename docker image"

docker build -t $dockerimagename $dockerfile

docker tag cmdbuild-oda:#cmdbuildversion# cmdbuild-oda:latest

echo "docker image created"
