# Nginx PHP

Sindria Base Image based on official alpine base image.

## Platforms

- amd64
- arm64

This set of images can be used with PHP project from version 7.1 to 8.1.

**Latest Tags:**

_General Purpose_

- sindriainc/nginx-php:6.0.0-7.1
- sindriainc/nginx-php:6.0.0-local-7.1
- sindriainc/nginx-php:6.0.0-7.2
- sindriainc/nginx-php:6.0.0-local-7.2
- sindriainc/nginx-php:6.0.0-7.3
- sindriainc/nginx-php:6.0.0-local-7.3
- sindriainc/nginx-php:6.0.0-7.4
- sindriainc/nginx-php:6.0.0-local-7.4
- sindriainc/nginx-php:6.0.0-8.0
- sindriainc/nginx-php:6.0.0-local-8.0
- sindriainc/nginx-php:6.0.0-8.1
- sindriainc/nginx-php:6.0.0-local-8.1

_Magento_

- sindriainc/nginx-php:6.0.0-magento-7.1
- sindriainc/nginx-php:6.0.0-local-magento-7.1
- sindriainc/nginx-php:6.0.0-magento-7.2
- sindriainc/nginx-php:6.0.0-local-magento-7.2
- sindriainc/nginx-php:6.0.0-magento-7.3
- sindriainc/nginx-php:6.0.0-local-magento-7.3
- sindriainc/nginx-php:6.0.0-magento-7.4
- sindriainc/nginx-php:6.0.0-local-magento-7.4
- sindriainc/nginx-php:6.0.0-magento-8.0
- sindriainc/nginx-php:6.0.0-local-magento-8.0

_Drupal_

- sindriainc/nginx-php:6.0.0-drupal-7.1
- sindriainc/nginx-php:6.0.0-local-drupal-7.1
- sindriainc/nginx-php:6.0.0-drupal-7.2
- sindriainc/nginx-php:6.0.0-local-drupal-7.2
- sindriainc/nginx-php:6.0.0-drupal-7.3
- sindriainc/nginx-php:6.0.0-local-drupal-7.3
- sindriainc/nginx-php:6.0.0-drupal-7.4
- sindriainc/nginx-php:6.0.0-local-drupal-7.4
- sindriainc/nginx-php:6.0.0-drupal-8.0
- sindriainc/nginx-php:6.0.0-local-drupal-8.0
- sindriainc/nginx-php:6.0.0-drupal-8.1
- sindriainc/nginx-php:6.0.0-local-drupal-8.1

_WordPress_

- sindriainc/nginx-php:6.0.0-wordpress-7.1
- sindriainc/nginx-php:6.0.0-local-wordpress-7.1
- sindriainc/nginx-php:6.0.0-wordpress-7.2
- sindriainc/nginx-php:6.0.0-local-wordpress-7.2
- sindriainc/nginx-php:6.0.0-wordpress-7.3
- sindriainc/nginx-php:6.0.0-local-wordpress-7.3
- sindriainc/nginx-php:6.0.0-wordpress-7.4
- sindriainc/nginx-php:6.0.0-local-wordpress-7.4
- sindriainc/nginx-php:6.0.0-wordpress-8.0
- sindriainc/nginx-php:6.0.0-local-wordpress-8.0
- sindriainc/nginx-php:6.0.0-wordpress-8.1
- sindriainc/nginx-php:6.0.0-local-wordpress-8.1

_SuiteCRM_

- sindriainc/nginx-php:6.0.0-suitecrm-7.1
- sindriainc/nginx-php:6.0.0-local-suitecrm-7.1
- sindriainc/nginx-php:6.0.0-suitecrm-7.2
- sindriainc/nginx-php:6.0.0-local-suitecrm-7.2
- sindriainc/nginx-php:6.0.0-suitecrm-7.3
- sindriainc/nginx-php:6.0.0-local-suitecrm-7.3
- sindriainc/nginx-php:6.0.0-suitecrm-7.4
- sindriainc/nginx-php:6.0.0-local-suitecrm-7.4

### Installation & Configuration

You can use docker-compose.example.yml configuration file as a sample, here you can find instructions
for deploying a new application.

#### Environment variables
- VIRTUAL_HOST Main domain of the application (e.g example.org)
- PHP_PM_MAX_CHILDREN Max number of child process for php-fpm pool 
- PHP_XDEBUG_IDE_KEY xdebug ide key, default is `PHPSTORM`

#### Installation instructions
- copy `config` directory in your project root.
- navigate to `config/php/cli` and `config/php/fpm`, copy ini configuration file corresponding to your
PHP version as `php.ini`
- navigate to `config/nginx/sites`, open `app.conf` and update it according to your needs
- if you have any cron job, you can add it to `config/cron.d/app`

## Release

Run utils script: `bash bin/release.sh <version>` eg. `bash bin/release.sh 1.0.0`
