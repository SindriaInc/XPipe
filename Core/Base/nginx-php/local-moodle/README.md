# Nginx PHP - Local Moodle

This set of images can be used with PHP project from version 7.4 to 8.3.

Available images:

- sindriainc/nginx-php:moodle-7.4
- sindriainc/nginx-php:local-moodle-7.4
- sindriainc/nginx-php:moodle-8.0
- sindriainc/nginx-php:local-moodle-8.0
- sindriainc/nginx-php:moodle-8.1
- sindriainc/nginx-php:local-moodle-8.1
- sindriainc/nginx-php:moodle-8.2
- sindriainc/nginx-php:local-moodle-8.2
- sindriainc/nginx-php:moodle-8.3
- sindriainc/nginx-php:local-moodle-8.3

#### Environment variables

- HOST_USER_UID default is "1000"
- TZ time zone default is "Europe/Rome"
- NGINX_PHP_PM_MAX_CHILDREN Max number of child process for php-fpm pool default is "16" 
- PHP_XDEBUG_IDE_KEY xdebug ide key, default is `PHPSTORM`