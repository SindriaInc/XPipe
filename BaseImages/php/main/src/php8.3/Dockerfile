ARG ARCH
FROM ${ARCH}/alpine:3.18

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
		php83-pear \
		php83-dev \
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
RUN apk add --no-cache php83 \
    php83-common \
    php83-cli \
    php83-opcache \
    php83-calendar \
    php83-ctype \
    php83-curl \
    php83-exif \
    php83-fileinfo \
    php83-ftp \
    php83-gd \
    php83-gettext \
    php83-iconv \
    php83-intl \
    php83-json \
    php83-mbstring \
    php83-mysqli \
    php83-pdo \
    php83-phar \
    php83-posix \
    php83-dom \
    #php83-readline \
    php83-shmop \
    php83-sockets \
    php83-sysvmsg \
    php83-tokenizer \
    php83-session \
    php83-openssl \
    php83-xml \
    php83-xmlwriter \
    php83-simplexml \
    php83-zip \
    php83-xsl \
    php83-bcmath \
    php83-imap \
    php83-soap \
    php83-pgsql \
    php83-pdo_pgsql \
    php83-pdo_mysql \
    php83-pdo_sqlite \
    php83-pdo_dblib \
    php83-pecl-redis \
    php83-pecl-imagick \
    php83-sodium \
    php83-pcntl \
    php83-tidy && \
    ln -s /etc/php83 /etc/php && \
    ln -s /usr/bin/php83 /usr/local/bin/php && \
    rm -rf /var/cache/apk/*

# Patch: HTTP Requests with multipart/form-data with PUT/PATCH methods
# Link: https://stackoverflow.com/questions/50691938/patch-and-put-request-does-not-working-with-form-data
# Solution: https://pecl.php.net/package/apfd
RUN apk add --no-cache --virtual .build-deps $PHPIZE_DEPS $PECL_DEPS && \
    pecl install apfd || true && \
    apk del -f .build-deps && \
    echo ";extension=apfd.so" >> /etc/php/php.ini

SHELL ["/bin/bash", "-c"]

# Add and setup entrypoint
COPY resources/bin/entrypoint.sh /usr/local/bin/entrypoint.sh
ENTRYPOINT ["/bin/bash", "entrypoint.sh"]

#USER ${SINDRIA_USER}