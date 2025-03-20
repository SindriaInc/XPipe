#!/bin/bash

#!/usr/bin/env bash

echo "Starting up using ${BOOTSTRAP_USER}: $(whoami):$(id -gn)"

# Override host user uid by env
if [ "${HOST_USER_UID}" != "1000" ]; then
  echo "Using host user id for ${SINDRIA_USER}: ${HOST_USER_UID}"

  sudo usermod -u ${HOST_USER_UID} ${SINDRIA_USER} && sudo groupmod ${SINDRIA_USER} -g ${HOST_USER_UID}

  case ${PHP_VERSION} in
    7.0)
        sudo chown ${SINDRIA_USER}:root /run/php/
        sudo chown -R ${SINDRIA_USER}:root /var/log/php7/
        sudo chown ${SINDRIA_USER}:${SINDRIA_USER} /var/log/php7/error.log
        ;;
    7.1)
        sudo chown ${SINDRIA_USER}:root /run/php/
        sudo chown -R ${SINDRIA_USER}:root /var/log/php7/
        sudo chown ${SINDRIA_USER}:${SINDRIA_USER} /var/log/php7/error.log
        ;;
    7.2)
        sudo chown ${SINDRIA_USER}:root /run/php/
        sudo chown -R ${SINDRIA_USER}:root /var/log/php7/
        sudo chown ${SINDRIA_USER}:${SINDRIA_USER} /var/log/php7/error.log
        ;;
    7.3)
        sudo chown ${SINDRIA_USER}:root /run/php/
        sudo chown -R ${SINDRIA_USER}:root /var/log/php7/
        sudo chown ${SINDRIA_USER}:${SINDRIA_USER} /var/log/php7/error.log
        ;;
    7.4)
        sudo chown ${SINDRIA_USER}:root /run/php/
        sudo chown -R ${SINDRIA_USER}:root /var/log/php7/
        sudo chown ${SINDRIA_USER}:${SINDRIA_USER} /var/log/php7/error.log
        ;;
    8.0)
        sudo chown ${SINDRIA_USER}:root /run/php/
        sudo chown -R ${SINDRIA_USER}:root /var/log/php8/
        sudo chown ${SINDRIA_USER}:${SINDRIA_USER} /var/log/php8/error.log
        ;;
    8.1)
        sudo chown ${SINDRIA_USER}:root /run/php/
        sudo chown -R ${SINDRIA_USER}:root /var/log/php81/
        sudo chown ${SINDRIA_USER}:${SINDRIA_USER} /var/log/php81/error.log
        ;;
    8.2)
        sudo chown ${SINDRIA_USER}:root /run/php/
        sudo chown -R ${SINDRIA_USER}:root /var/log/php82/
        sudo chown ${SINDRIA_USER}:${SINDRIA_USER} /var/log/php82/error.log
        ;;
    8.3)
        sudo chown ${SINDRIA_USER}:root /run/php/
        sudo chown -R ${SINDRIA_USER}:root /var/log/php83/
        sudo chown ${SINDRIA_USER}:${SINDRIA_USER} /var/log/php83/error.log
        ;;
    *)
        echo "Invalid PHP version specified"
        exit 1
    ;;
  esac

  # Fix static UID
  sudo chown -R ${SINDRIA_USER}:${SINDRIA_USER} /run/nginx
  sudo chown -R ${SINDRIA_USER}:${SINDRIA_USER} /var/log/nginx/
  sudo chown ${SINDRIA_USER}:${SINDRIA_USER} /var/log/nginx/error.log
  sudo chown ${SINDRIA_USER}:${SINDRIA_USER} /var/log/nginx/access.log
  sudo chown -R ${SINDRIA_USER}:${SINDRIA_USER} /var/lib/nginx
  sudo chown -R ${SINDRIA_USER}:${SINDRIA_USER} /var/lib/nginx/logs
  sudo chown ${SINDRIA_USER}:${SINDRIA_USER} /var/lib/nginx/logs/error.log
  sudo chown ${SINDRIA_USER}:${SINDRIA_USER} /var/lib/nginx/logs/access.log
  sudo chown -R ${SINDRIA_USER}:${SINDRIA_USER} /var/lib/nginx/tmp/

  sudo chown -R ${SINDRIA_USER}:root /var/run/supervisor
  sudo chown -R ${SINDRIA_USER}:${SINDRIA_USER} /var/log/supervisor
fi

sudo chmod 644 /etc/hosts
sudo chown ${BOOTSTRAP_USER}:root /etc/hosts

CONTAINER_GATEWAY=`sudo /sbin/ip route|awk '/default/ { print $3 }'`

# Adding host machine hostname
echo -e "\n# Hostname for gateway" | sudo tee -a /etc/hosts > /dev/null
echo -e "${CONTAINER_GATEWAY}\tdocker.host.internal\n" | sudo tee -a /etc/hosts > /dev/null

# Adding variables to php-fpm pool configuration
sudo sed -i -E "s|\[@@POOL_NAME@@\]|\[${HOSTNAME}-php-fpm-pool\]|g" /etc/php/php-fpm.d/sindria.conf

# Change max children php-fpm pool configuration
if [ "${PHP_PM_MAX_CHILDREN}" != "16" ]; then
    sudo sed -i -E "s|pm.max_children = ([0-9]+)|pm.max_children = ${PHP_PM_MAX_CHILDREN}|g" /etc/php/php-fpm.d/sindria.conf
fi

# Override nginx virtualhost configuration
if [ -e /home/sindria/config/nginx/sites-available/app.conf ]; then
    sudo cp /home/sindria/config/nginx/sites-available/app.conf /etc/nginx/sites-enabled/
fi

# Override php.ini fpm if config file exists
if [ -e /home/sindria/config/php/fpm/php.ini ]; then
    sudo cp /home/sindria/config/php/fpm/php.ini /etc/php/${PHP_VERSION}/fpm/php.ini
fi

# Override php.ini cli if config file exists
if [ -e /home/sindria/config/php/cli/php.ini ]; then
    sudo cp /home/sindria/config/php/cli/php.ini /etc/php/${PHP_VERSION}/cli/php.ini
fi

# Override timezone by env
if [ "$TZ" != "Europe/Rome" ]; then
    sudo ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
fi

sudo chown -R ${SINDRIA_USER}:${SINDRIA_USER} /etc/nginx

# Start nginx and php-fpm daemons
sudo --preserve-env su ${SINDRIA_USER} -c "/usr/bin/supervisord -n -c /etc/supervisor/supervisord.conf"&


curl 'http://127.0.0.1/wp-admin/install.php?step=2' --compressed -X POST  --data-raw 'weblog_title='${WP_WEBBLOG_TITLE}'&user_name='${WP_USER_NAME}'&admin_password='${WP_ADMIN_PW}'&admin_password2='${WP_ADMIN_PW}'&pw_weak='${WP_PW_WEAK}'&admin_email='${WP_ADMIN_EMAIL}