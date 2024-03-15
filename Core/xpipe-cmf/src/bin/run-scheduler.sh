#!/usr/bin/env bash

#* * * * * cd /path-to-your-project && php artisan schedule:run >> /dev/null 2>&1

while [ true ]; do
  php /var/www/app/artisan schedule:run --verbose --no-interaction &
  sleep 60
done