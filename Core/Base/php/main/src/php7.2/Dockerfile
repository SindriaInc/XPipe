ARG ARCH
FROM ${ARCH}/alpine:3.9

ARG TAG_SUFFIX
ARG TAG_VERSION
ARG HOST_USER_UID
ARG BOOTSTRAP_HOST_USER_UID
ARG TIMEZONE

LABEL \
	name="php" \
	image="sindriainc/php" \
	tag="${TAG_VERSION}-${TAG_SUFFIX}" \
	vendor="sindria"

ENV PATH "/root/.local/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/home/sindria/.local/bin"
ENV TZ=${TIMEZONE}
ENV SINDRIA_USER="sindria"
ENV SINDRIA_USER_HOME="/home/sindria"
ENV BOOTSTRAP_USER="bootstrap"
ENV BOOTSTRAP_USER_HOME="/home/bootstrap"
ENV PHP_VERSION=${TAG_SUFFIX}

# dependencies required for running "phpize"
# these get automatically installed and removed by "docker-php-ext-*" (unless they're already installed)
ENV PHPIZE_DEPS \
		autoconf \
		dpkg-dev dpkg \
		file \
		g++ \
		gcc \
		libc-dev \
		make \
		pkgconf \
		re2c

# dependencies required for running "pecl"
ENV PECL_DEPS \
		php7-pear \
		php7-dev \
		gcc \
		musl-dev \
		make

#COPY resources/bin/ /usr/local/bin/

# Update and install packages
RUN apk update && \
    apk add \
    shadow \
    bash \
    curl \
    wget \
    git \
    rsync \
    openssl \
    ca-certificates \
    tzdata && \
    rm -rf /var/cache/apk/*

# Setting boostrap user
RUN addgroup -S ${BOOTSTRAP_USER} -g ${BOOTSTRAP_HOST_USER_UID} && adduser -s /bin/bash -S ${BOOTSTRAP_USER} -u ${BOOTSTRAP_HOST_USER_UID} -G ${BOOTSTRAP_USER} -h ${BOOTSTRAP_USER_HOME}
COPY resources/bootstrap/.bashrc ${BOOTSTRAP_USER_HOME}

# Setting app user, timezone and permissions
RUN addgroup -S ${SINDRIA_USER} -g ${HOST_USER_UID} && adduser -s /bin/bash -S ${SINDRIA_USER} -u ${HOST_USER_UID} -G ${SINDRIA_USER} -h ${SINDRIA_USER_HOME} && \
    mkdir -p /var/www && \
    mkdir -p /var/www/app && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && \
    chmod -R 770 /var/www/app && \
    chown -R ${SINDRIA_USER}:${SINDRIA_USER} /var/www/app
COPY resources/sindria/.bashrc ${SINDRIA_USER_HOME}

WORKDIR /var/www/app

# Installing PHP with extensions
RUN apk add --no-cache php7 \
    php7-common \
    php7-cli \
    php7-opcache \
    php7-calendar \
    php7-ctype \
    php7-curl \
    php7-exif \
    php7-fileinfo \
    php7-ftp \
    php7-gd \
    php7-gettext \
    php7-iconv \
    php7-intl \
    php7-json \
    php7-mbstring \
    php7-mysqli \
    php7-pdo \
    php7-phar \
    php7-posix \
    php7-dom \
    #php7-readline \
    php7-shmop \
    php7-sockets \
    php7-sysvmsg \
    php7-tokenizer \
    php7-session \
    php7-openssl \
    php7-xml \
    php7-xmlwriter \
    php7-simplexml \
    php7-zip \
    php7-xsl \
    php7-bcmath \
    php7-imap \
    php7-soap \
    php7-pgsql \
    php7-pdo_pgsql \
    php7-pdo_mysql \
    php7-pdo_sqlite \
    php7-pdo_dblib \
    php7-pecl-redis \
    php7-pecl-imagick \
    php7-sodium \
    php7-pcntl \
    php7-tidy && \
    ln -s /etc/php7 /etc/php && \
    rm -rf /var/cache/apk/*

# Patch: HTTP Requests with multipart/form-data with PUT/PATCH methods
# Link: https://stackoverflow.com/questions/50691938/patch-and-put-request-does-not-working-with-form-data
# Solution: https://pecl.php.net/package/apfd
RUN apk add --no-cache --virtual .build-deps $PHPIZE_DEPS $PECL_DEPS && \
    pecl install apfd || true && \
    apk del -f .build-deps && \
    echo "extension=apfd.so" >> /etc/php/php.ini

SHELL ["/bin/bash", "-c"]

# Add and setup entrypoint
COPY resources/bin/entrypoint.sh /usr/local/bin/entrypoint.sh
ENTRYPOINT ["/bin/bash", "entrypoint.sh"]

#USER ${SINDRIA_USER}