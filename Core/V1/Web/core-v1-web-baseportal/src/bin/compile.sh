#!/usr/bin/env bash

php bin/magento setup:di:compile
php bin/magento setup:static-content:deploy -f
