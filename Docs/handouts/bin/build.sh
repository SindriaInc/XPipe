#!/usr/bin/env bash

CONTAINER_NAME=handouts
FILEMD=cloud.md
FILENAME=corso_cloud_modulo1_v1.pdf

docker exec -it ${CONTAINER_NAME} pandoc ${FILEMD} -o out.pdf
docker exec -it ${CONTAINER_NAME} mv /var/www/app/out.pdf /var/www/app/out/${FILENAME}