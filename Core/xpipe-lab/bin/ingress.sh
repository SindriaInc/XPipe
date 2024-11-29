#!/usr/bin/env bash

CONTAINER_NAME=xpipe-lab-control-plane
docker exec -it ${CONTAINER_NAME} kubectl apply -f https://kind.sigs.k8s.io/examples/ingress/deploy-ingress-nginx.yaml
