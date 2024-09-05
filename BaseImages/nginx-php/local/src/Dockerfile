ARG ARCH
ARG TAG_SUFFIX
FROM sindriainc/nginx-php:6.1.0-${TAG_SUFFIX}-${ARCH}

ARG TAG_VERSION
ARG TAG_SUFFIX
ARG HOST_USER_UID
ARG TIMEZONE

LABEL \
	name="sindriainc/nginx-php" \
	image="nginx-php" \
	tag="${TAG_VERSION}-${TAG_SUFFIX}" \
	vendor="sindria"

ENV HOST_USER_UID ${HOST_USER_UID}
ENV TZ=${TIMEZONE}
ENV SINDRIA_USER="sindria"
ENV SINDRIA_USER_HOME="/home/sindria"
ENV SINDRIA_USER_PASSWORD="sindria"
ENV PHP_VERSION=${TAG_SUFFIX}
ENV PHP_PM_MAX_CHILDREN=16
ENV PHP_XDEBUG_MODE debug
ENV PHP_XDEBUG_START_WITH_REQUEST yes
ENV PHP_XDEBUG_HOST docker.host.internal
ENV PHP_XDEBUG_PORT 9000
ENV PHP_XDEBUG_IDE_KEY PHPSTORM

USER root

# Update and install packages
RUN apk update && \
    apk add \
    wget \
    vim \
    unzip \
    git \
    rsync && \
    rm -rf /var/cache/apk/*

# Install dev tools
RUN curl -sS https://getcomposer.org/installer -o /tmp/composer-setup.php && \
    php /tmp/composer-setup.php --install-dir=/usr/local/bin --filename=composer && \
    apk add --no-cache --virtual .build-deps $PHPIZE_DEPS $PECL_DEPS && \
    pecl install xdebug-3.0.0 || pecl install xdebug-2.6.0 || true && \
    apk del -f .build-deps && \
    touch /etc/php/conf.d/xdebug.ini && \
    echo "#zend_extension=/usr/lib/php7/modules/xdebug.so" >> /etc/php/conf.d/xdebug.ini && \
    echo "#zend_extension=/usr/lib/php8/modules/xdebug.so" >> /etc/php/conf.d/xdebug.ini && \
    echo "" >> /etc/php/conf.d/xdebug.ini && \
    echo "xdebug.mode=${PHP_XDEBUG_MODE}" >> /etc/php/conf.d/xdebug.ini && \
    echo "xdebug.start_with_request=${PHP_XDEBUG_START_WITH_REQUEST}" >> /etc/php/conf.d/xdebug.ini && \
    echo "xdebug.client_host=${PHP_XDEBUG_HOST}" >> /etc/php/conf.d/xdebug.ini && \
    echo "xdebug.client_port=${PHP_XDEBUG_PORT}" >> /etc/php/conf.d/xdebug.ini && \
    echo "xdebug.idekey=${PHP_XDEBUG_IDE_KEY}" >> /etc/php/conf.d/xdebug.ini && \
    echo "xdebug.log=/var/log/xdebug.log" >> /etc/php/conf.d/xdebug.ini && \
    touch /var/log/xdebug.log && \
    curl -L https://cs.symfony.com/download/php-cs-fixer-v2.phar -o /usr/local/bin/php-cs-fixer && \
    chmod +x /usr/local/bin/php-cs-fixer && \
    curl -L https://squizlabs.github.io/PHP_CodeSniffer/phpcs.phar -o /usr/local/bin/phpcs && \
    curl -L https://squizlabs.github.io/PHP_CodeSniffer/phpcbf.phar -o /usr/local/bin/phpcbf && \
    chmod +x /usr/local/bin/phpcs && \
    chmod +x /usr/local/bin/phpcbf

# Install ssh server
COPY resources/supervisor/conf.d/sshd.conf /etc/supervisor/conf.d/
COPY resources/ssh/sshd_config ${SINDRIA_USER_HOME}/custom_ssh/sshd_config
RUN apk add --no-cache \
    openssh-server \
    openssh-sftp-server \
    openssh-server-pam && \
    mkdir -p ${SINDRIA_USER_HOME}/custom_ssh && \
    ssh-keygen -f ${SINDRIA_USER_HOME}/custom_ssh/ssh_host_rsa_key -N '' -t rsa && \
    ssh-keygen -f ${SINDRIA_USER_HOME}/custom_ssh/ssh_host_dsa_key -N '' -t dsa && \
    chown -R ${SINDRIA_USER}:${SINDRIA_USER} ${SINDRIA_USER_HOME}/custom_ssh && \
    echo "${SINDRIA_USER}:${SINDRIA_USER_PASSWORD}" | chpasswd

USER ${SINDRIA_USER}