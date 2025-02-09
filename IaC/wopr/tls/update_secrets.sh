#!/usr/bin/env bash

# Delete local cached directories
rm -rf monster-hunter.it
rm -rf monsterhunter.it
rm -rf liviomunaridecorazioni.it

# export AWS_PROFILE=devops
AWS_PROFILE=devops

# Sync liviomunaridecorazioni.it
aws s3 sync s3://sindria-lmd-certbot-cache/k8s/ .

# Sync monsterhunter.it and monster-hunter.it
aws s3 sync s3://sindria-mh-certbot-cache/k8s/ .

# Update liviomunaridecorazioni.it
kubectl apply -f liviomunaridecorazioni.it/tls-liviomunaridecorazioniit.yaml -n lmd-website

# Update monsterhunter.it
kubectl apply -f monsterhunter.it/tls-monsterhunterit.yaml -n mh-website
# Update monster-hunter.it
kubectl apply -f monster-hunter.it/tls-monster-hunterit.yaml -n mh-website
