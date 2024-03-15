#!/usr/bin/env bash

CONTAINER_GATEWAY=`/sbin/ip route|awk '/default/ { print $3 }'`

# Adding host machine hostname
echo -e "\n# Hostname for gateway" >> /etc/hosts
echo -e "${CONTAINER_GATEWAY}\tdocker.host.internal\n" >> /etc/hosts

# Override xdebug ide key
if [ "${PHP_XDEBUG_IDE_KEY}" != "PHPSTORM" ]; then
    sed -i -E "s|PHPSTORM|${PHP_XDEBUG_IDE_KEY}|g" /usr/local/etc/php/conf.d/xdebug.ini
fi

# Override timezone by env
if [ "$TZ" != "" ] || [ "$TZ" != "Europe/Rome" ]; then
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
fi

# Override host user uid by env
if [ "$HOST_USER_UID" != "1000" ]; then
    usermod -u $HOST_USER_UID sindria && groupmod sindria -g $HOST_USER_UID
fi