#!/usr/bin/env bash


if [ "$TAG_VERSION" == "production" ]; then
    rm -f /etc/hosts.local
    rm -f /etc/hosts.development
    rm -f /etc/hosts.staging
fi

if [ "$TAG_VERSION" == "staging" ]; then
    rm -f /etc/hosts.local
    rm -f /etc/hosts.development
    rm -f /etc/hosts.production
fi

if [ "$TAG_VERSION" == "development" ]; then
    rm -f /etc/hosts.local
    rm -f /etc/hosts.staging
    rm -f /etc/hosts.production
fi

if [ "$TAG_VERSION" == "local" ]; then
    rm -f /etc/hosts.production
    rm -f /etc/hosts.staging
    rm -f /etc/hosts.development
fi
