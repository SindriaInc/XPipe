#!/usr/bin/env bash

set -e

# Setting Colors
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
NC='\033[0m' #No Color
NOW=$(date "+%Y-%m-%d_%H-%M-%S")

BASE_PATH=$HOME
CLUSTER_NAME=$1

if [[ -z "$1" ]]; then
    echo -e "${YELLOW}Provide cluster name as first argument (eg. xpipe-lab)${NC}"
    echo
    echo -e "${BLUE}Available clusters: ${NC}"    
    find ${BASE_PATH}/.kube/clusters -type d | cut -d '/' -f 6    
    exit 1
fi

CURRENT_CLUSTER=$(echo $KUBECONFIG | cut -d '/' -f 6)

if [[ "${CLUSTER_NAME}" == "current" ]]; then
    echo -e "${BLUE}Current cluster: ${NC}"
    echo $CURRENT_CLUSTER
    exit 0
fi

cd ${BASE_PATH}/.kube/clusters/${CLUSTER_NAME}
export KUBECONFIG=${BASE_PATH}/.kube/clusters/${CLUSTER_NAME}/kubeconfig
exec $SHELL -i	
