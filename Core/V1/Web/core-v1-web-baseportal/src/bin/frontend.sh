#!/usr/bin/env bash

rm -r pub/static/*/*
rm -r var/view_preprocessed/*
php bin/magento cache:clean
