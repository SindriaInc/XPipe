#!/usr/bin/env bash

# Delete local cached directories
#rm -rf sindria.org

# export AWS_PROFILE=default
#AWS_PROFILE=default

# Sync sindria.org from cache
#aws s3 sync s3://sindria-devops-certbot-cache/k8s/ .

# Update sindria.org on all namespaces
kubectl apply -f sindria.org/tls-sindriaorg.yaml -n bastion
#kubectl apply -f sindria.org/tls-sindriaorg.yaml -n cdn
kubectl apply -f sindria.org/tls-sindriaorg.yaml -n monitoring
kubectl apply -f sindria.org/tls-sindriaorg.yaml -n sindria-mc
#kubectl apply -f sindria.org/tls-sindriaorg.yaml -n sindria-sec
kubectl apply -f sindria.org/tls-sindriaorg.yaml -n xpipe-cloud
#kubectl apply -f sindria.org/tls-sindriaorg.yaml -n xpipe-demo
kubectl apply -f sindria.org/tls-sindriaorg.yaml -n xpipe-dev
#kubectl apply -f sindria.org/tls-sindriaorg.yaml -n xpipe-sindria
kubectl apply -f sindria.org/tls-sindriaorg.yaml -n xpipe-lucapitzoi
kubectl apply -f sindria.org/tls-sindriaorg.yaml -n xpipe-dorjecurreli
