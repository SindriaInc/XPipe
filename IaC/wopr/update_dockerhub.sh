#!/usr/bin/env bash

# export AWS_PROFILE=default
#AWS_PROFILE=default

# Delete existing docker hub secret on all namespaces
kubectl delete secret sindria-dockerhub -n bastion
#kubectl delete secret sindria-dockerhub -n cdn
kubectl delete secret sindria-dockerhub -n monitoring
kubectl delete secret sindria-dockerhub -n mh-website
kubectl delete secret sindria-dockerhub -n lmd-website
kubectl delete secret sindria-dockerhub -n sindria-mc
#kubectl delete secret sindria-dockerhub -n sindria-sec
kubectl delete secret sindria-dockerhub -n xpipe-cloud
#kubectl delete secret sindria-dockerhub -n xpipe-demo
kubectl delete secret sindria-dockerhub -n xpipe-dev
#kubectl delete secret sindria-dockerhub -n xpipe-sindria
kubectl delete secret sindria-dockerhub -n xpipe-lucapitzoi
kubectl delete secret sindria-dockerhub -n xpipe-dorjecurreli


# Update docker hub secret on all namespaces
kubectl apply -f sindria-dockerhub.yaml -n bastion
#kubectl apply -f sindria-dockerhub.yaml -n cdn
kubectl apply -f sindria-dockerhub.yaml -n monitoring
kubectl apply -f sindria-dockerhub.yaml -n mh-website
kubectl apply -f sindria-dockerhub.yaml -n lmd-website
kubectl apply -f sindria-dockerhub.yaml -n sindria-mc
#kubectl apply -f sindria-dockerhub.yaml -n sindria-sec
kubectl apply -f sindria-dockerhub.yaml -n xpipe-cloud
#kubectl apply -f sindria-dockerhub.yaml -n xpipe-demo
kubectl apply -f sindria-dockerhub.yaml -n xpipe-dev
#kubectl apply -f sindria-dockerhub.yaml -n xpipe-sindria
kubectl apply -f sindria-dockerhub.yaml -n xpipe-lucapitzoi
kubectl apply -f sindria-dockerhub.yaml -n xpipe-dorjecurreli
