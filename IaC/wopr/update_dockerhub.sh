#!/usr/bin/env bash

# export AWS_PROFILE=default
#AWS_PROFILE=default

# Update docker hub secret on all namespaces
kubectl apply -f sindria-dockerhub.yaml -n bastion
kubectl apply -f sindria-dockerhub.yaml -n cdn
kubectl apply -f sindria-dockerhub.yaml -n monitoring
kubectl apply -f sindria-dockerhub.yaml -n sindria-mc
kubectl apply -f sindria-dockerhub.yaml -n sindria-sec
kubectl apply -f sindria-dockerhub.yaml -n xpipe-cloud
kubectl apply -f sindria-dockerhub.yaml -n xpipe-demo
kubectl apply -f sindria-dockerhub.yaml -n xpipe-dev
kubectl apply -f sindria-dockerhub.yaml -n xpipe-sindria
