#!/usr/bin/env bash

PHP_VERSION=$1

apk update

case ${PHP_VERSION} in
    7.0)
        apk add php7-fpm
        ln -s /usr/sbin/php-fpm7 /usr/sbin/php-fpm
        ln -s /etc/php7 /etc/php
        mkdir -p /run/php/
        rm /etc/php7/php-fpm.d/www.conf
        cp ${SINDRIA_USER_HOME}/sindria.conf /etc/php7/php-fpm.d/sindria.conf
        chown ${SINDRIA_USER}:root /run/php/
        chmod 755 /var/log/php7/
        chown -R ${SINDRIA_USER}:root /var/log/php7/
        touch /var/log/php7/error.log
        touch /var/log/php7/access.log
        chown ${SINDRIA_USER}:${SINDRIA_USER} /var/log/php7/error.log
        ln -sf /dev/stderr /var/log/php7/access.log
        ln -sf /dev/stderr /var/log/php7/error.log
        ;;
    7.1)
        apk add php7-fpm
        ln -s /usr/sbin/php-fpm7 /usr/sbin/php-fpm
        ln -s /etc/php7 /etc/php
        mkdir -p /run/php/
        rm /etc/php7/php-fpm.d/www.conf
        cp ${SINDRIA_USER_HOME}/sindria.conf /etc/php7/php-fpm.d/sindria.conf
        chown ${SINDRIA_USER}:root /run/php/
        chmod 755 /var/log/php7/
        chown -R ${SINDRIA_USER}:root /var/log/php7/
        touch /var/log/php7/error.log
        touch /var/log/php7/access.log
        chown ${SINDRIA_USER}:${SINDRIA_USER} /var/log/php7/error.log
        ln -sf /dev/stderr /var/log/php7/access.log
        ln -sf /dev/stderr /var/log/php7/error.log
        ;;
    7.2)
        apk add php7-fpm
        ln -s /usr/sbin/php-fpm7 /usr/sbin/php-fpm
        ln -s /etc/php7 /etc/php
        mkdir -p /run/php/
        rm /etc/php7/php-fpm.d/www.conf
        cp ${SINDRIA_USER_HOME}/sindria.conf /etc/php7/php-fpm.d/sindria.conf
        chown ${SINDRIA_USER}:root /run/php/
        chmod 755 /var/log/php7/
        chown -R ${SINDRIA_USER}:root /var/log/php7/
        touch /var/log/php7/error.log
        touch /var/log/php7/access.log
        chown ${SINDRIA_USER}:${SINDRIA_USER} /var/log/php7/error.log
        ln -sf /dev/stderr /var/log/php7/access.log
        ln -sf /dev/stderr /var/log/php7/error.log
        ;;
    7.3)
        apk add php7-fpm
        ln -s /usr/sbin/php-fpm7 /usr/sbin/php-fpm
        ln -s /etc/php7 /etc/php
        mkdir -p /run/php/
        rm /etc/php7/php-fpm.d/www.conf
        cp ${SINDRIA_USER_HOME}/sindria.conf /etc/php7/php-fpm.d/sindria.conf
        chown ${SINDRIA_USER}:root /run/php/
        chmod 755 /var/log/php7/
        chown -R ${SINDRIA_USER}:root /var/log/php7/
        touch /var/log/php7/error.log
        touch /var/log/php7/access.log
        chown ${SINDRIA_USER}:${SINDRIA_USER} /var/log/php7/error.log
        ln -sf /dev/stderr /var/log/php7/access.log
        ln -sf /dev/stderr /var/log/php7/error.log
        ;;
    7.4)
        apk add php7-fpm
        ln -s /usr/sbin/php-fpm7 /usr/sbin/php-fpm
        ln -s /etc/php7 /etc/php
        mkdir -p /run/php/
        rm /etc/php7/php-fpm.d/www.conf
        cp ${SINDRIA_USER_HOME}/sindria.conf /etc/php7/php-fpm.d/sindria.conf
        chown ${SINDRIA_USER}:root /run/php/
        chmod 755 /var/log/php7/
        chown -R ${SINDRIA_USER}:root /var/log/php7/
        touch /var/log/php7/error.log
        touch /var/log/php7/access.log
        chown ${SINDRIA_USER}:${SINDRIA_USER} /var/log/php7/error.log
        ln -sf /dev/stderr /var/log/php7/access.log
        ln -sf /dev/stderr /var/log/php7/error.log
        ;;
    8.0)
        apk add php8-fpm
        ln -s /usr/sbin/php-fpm8 /usr/sbin/php-fpm
        ln -s /etc/php8 /etc/php
        mkdir -p /run/php/
        rm /etc/php8/php-fpm.d/www.conf
        cp ${SINDRIA_USER_HOME}/sindria.conf /etc/php8/php-fpm.d/sindria.conf
        chown ${SINDRIA_USER}:root /run/php/
        chmod 755 /var/log/php8/
        chown -R ${SINDRIA_USER}:root /var/log/php8/
        touch /var/log/php8/error.log
        touch /var/log/php8/access.log
        chown ${SINDRIA_USER}:${SINDRIA_USER} /var/log/php8/error.log
        ln -sf /dev/stderr /var/log/php8/access.log
        ln -sf /dev/stderr /var/log/php8/error.log
        ;;
    8.1)
        apk add php81-fpm
        ln -s /usr/sbin/php-fpm81 /usr/sbin/php-fpm
        ln -s /etc/php81 /etc/php
        mkdir -p /run/php/
        rm /etc/php81/php-fpm.d/www.conf
        cp ${SINDRIA_USER_HOME}/sindria.conf /etc/php81/php-fpm.d/sindria.conf
        chown ${SINDRIA_USER}:root /run/php/
        chmod 755 /var/log/php81/
        chown -R ${SINDRIA_USER}:root /var/log/1/
        touch /var/log/php81/error.log
        touch /var/log/php81/access.log
        chown ${SINDRIA_USER}:${SINDRIA_USER} /var/log/php81/error.log
        ln -sf /dev/stderr /var/log/php81/access.log
        ln -sf /dev/stderr /var/log/php81/error.log
        ;;
    8.2)
        apk add php82-fpm
        ln -s /usr/sbin/php-fpm82 /usr/sbin/php-fpm
        ln -s /etc/php82 /etc/php
        mkdir -p /run/php/
        rm /etc/php82/php-fpm.d/www.conf
        cp ${SINDRIA_USER_HOME}/sindria.conf /etc/php82/php-fpm.d/sindria.conf
        chown ${SINDRIA_USER}:root /run/php/
        chmod 755 /var/log/php82/
        chown -R ${SINDRIA_USER}:root /var/log/1/
        touch /var/log/php82/error.log
        touch /var/log/php82/access.log
        chown ${SINDRIA_USER}:${SINDRIA_USER} /var/log/php82/error.log
        ln -sf /dev/stderr /var/log/php82/access.log
        ln -sf /dev/stderr /var/log/php82/error.log
        ;;
    8.3)
        apk add php83-fpm
        ln -s /usr/sbin/php-fpm83 /usr/sbin/php-fpm
        ln -s /etc/php83 /etc/php
        mkdir -p /run/php/
        rm /etc/php83/php-fpm.d/www.conf
        cp ${SINDRIA_USER_HOME}/sindria.conf /etc/php83/php-fpm.d/sindria.conf
        chown ${SINDRIA_USER}:root /run/php/
        chmod 755 /var/log/php83/
        chown -R ${SINDRIA_USER}:root /var/log/1/
        touch /var/log/php83/error.log
        touch /var/log/php83/access.log
        chown ${SINDRIA_USER}:${SINDRIA_USER} /var/log/php83/error.log
        ln -sf /dev/stderr /var/log/php83/access.log
        ln -sf /dev/stderr /var/log/php83/error.log
        ;;
    *)
        echo "Invalid PHP version specified"
        exit 1
    ;;
esac
