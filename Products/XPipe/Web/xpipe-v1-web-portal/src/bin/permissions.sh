#!/usr/bin/env bash

find . -type f -exec chmod 664 {} \;
find . -type d -exec chmod 775 {} \;
find var pub/static pub/media app/etc -type f -exec chmod g+w {} \;
find var pub/static pub/media app/etc -type d -exec chmod g+ws {} \;
chmod u+x bin/magento
chown -R sindria:sindria /var/www/app
