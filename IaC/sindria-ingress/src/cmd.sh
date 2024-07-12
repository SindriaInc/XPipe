#!/usr/bin/env bash


if [ "$TAG_VERSION" == "production" ]; then
    cat /etc/hosts.production >> /etc/hosts
fi

if [ "$TAG_VERSION" == "staging" ]; then
    cat /etc/hosts.staging >> /etc/hosts
fi

if [ "$TAG_VERSION" == "development" ]; then
    cat /etc/hosts.development >> /etc/hosts
fi

if [ "$TAG_VERSION" == "local" ]; then
    cat /etc/hosts.local >> /etc/hosts
fi

exec nginx -g 'daemon off;'
