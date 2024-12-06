#!/usr/bin/env bash

CONTAINER_NAME=handouts
FILEMD=doingdots.md
FILENAME=analisi_preventivo_doingdots_v1.pdf

docker exec -it ${CONTAINER_NAME} pandoc ${FILEMD} -o out.pdf
docker exec -it ${CONTAINER_NAME} mv /var/www/app/out.pdf /var/www/app/out/${FILENAME}