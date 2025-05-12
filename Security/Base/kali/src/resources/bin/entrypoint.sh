#!/usr/bin/env bash

# Fix permission
chown -R ${SINDRIA_USER}:${SINDRIA_USER} /var/www/app

# Override timezone by env
if [ "${TZ}" != "" ] || [ "${TZ}" != "Europe/Rome" ]; then
    ln -snf /usr/share/zoneinfo/${TZ} /etc/localtime && echo ${TZ} > /etc/timezone
fi

# Override host user uid by env
if [ "${HOST_USER_UID}" != "1000" ]; then
    usermod -u ${HOST_USER_UID} ${SINDRIA_USER} && groupmod ${SINDRIA_USER} -g ${HOST_USER_UID}
fi


#tail -f /dev/null