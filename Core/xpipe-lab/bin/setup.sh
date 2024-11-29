#!/usr/bin/env bash

set -e

# Setting Colors
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
NC='\033[0m' #No Color
NOW=$(date "+%Y-%m-%d_%H-%M-%S")

echo -e "${BLUE}Installing ingress...${NC}"
# Setup ingress nginx
CONTAINER_NAME=xpipe-lab-control-plane
docker exec -it ${CONTAINER_NAME} kubectl apply -f https://kind.sigs.k8s.io/examples/ingress/deploy-ingress-nginx.yaml
echo

echo -e "${BLUE}Exporting kubeconfig...${NC}"
# Export kubeconfig
mkdir -p $HOME/.kube
mkdir -p $HOME/.kube/clusters
mkdir -p $HOME/.kube/clusters/xpipe-lab
touch $HOME/.kube/clusters/xpipe-lab/kubeconfig
docker exec -it ${CONTAINER_NAME} cat /etc/kubernetes/admin.conf > $HOME/.kube/clusters/xpipe-lab/kubeconfig
echo

echo -e "${BLUE}Done.${NC}"