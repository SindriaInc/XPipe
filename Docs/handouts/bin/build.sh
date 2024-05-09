#!/usr/bin/env bash

CONTAINER_NAME=handouts
FILEMD=questions_foundation.md
FILENAME=corso_fondamenti_di_informatica_modulo1_v1.pdf

docker exec -it ${CONTAINER_NAME} pandoc ${FILEMD} -o out.pdf
docker exec -it ${CONTAINER_NAME} mv /var/www/app/out.pdf /var/www/app/out/${FILENAME}