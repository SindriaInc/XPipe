# Nginx PHP - Local WordPress

This set of images can be used with PHP project from version 7.0 to 8.

Available images:

- sindriainc/nginx-php:wordpress-7.0
- sindriainc/nginx-php:local-wordpress-7.0
- sindriainc/nginx-php:wordpress-7.1
- sindriainc/nginx-php:local-wordpress-7.1
- sindriainc/nginx-php:wordpress-7.2
- sindriainc/nginx-php:local-wordpress-7.2
- sindriainc/nginx-php:wordpress-7.3
- sindriainc/nginx-php:local-wordpress-7.3
- sindriainc/nginx-php:wordpress-7.4
- sindriainc/nginx-php:local-wordpress-7.4
- sindriainc/nginx-php:wordpress-8
- sindriainc/nginx-php:local-wordpress-8

Legacy images:

- sindriainc/nginx-php:wordpress-7.1-local
- sindriainc/nginx-php:wordpress-7.1-production
- sindriainc/nginx-php:wordpress-7.2-local
- sindriainc/nginx-php:wordpress-7.2-production
- sindriainc/nginx-php:wordpress-7.3-local
- sindriainc/nginx-php:wordpress-7.3-production
- sindriainc/nginx-php:wordpress-7.4-local
- sindriainc/nginx-php:wordpress-7.4-production

#### Environment variables

- HOST_USER_UID default is "1000"
- TZ time zone default is "Europe/Rome"
- NGINX_PHP_PM_MAX_CHILDREN Max number of child process for php-fpm pool default is "16" 
- PHP_XDEBUG_IDE_KEY xdebug ide key, default is `PHPSTORM`