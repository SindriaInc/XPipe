#!/usr/bin/env bash

if [ "$TAG_VERSION" == "production" ]; then
    cp /etc/nginx/tmp/*.conf /etc/nginx/conf.d
    rm -f /etc/nginx/conf.d/local-*.conf
    rm -f /etc/nginx/conf.d/development-*.conf
    rm -f /etc/nginx/conf.d/staging-*.conf
fi

if [ "$TAG_VERSION" == "staging" ]; then
    cp /etc/nginx/tmp/staging-*.conf /etc/nginx/conf.d
fi

if [ "$TAG_VERSION" == "development" ]; then
    cp /etc/nginx/tmp/development-*.conf /etc/nginx/conf.d
fi

if [ "$TAG_VERSION" == "local" ]; then
    cp /etc/nginx/tmp/local-*.conf /etc/nginx/conf.d
fi

rm -rf /etc/nginx/tmp
