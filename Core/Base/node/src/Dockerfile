ARG ARCH
ARG TAG_SUFFIX
FROM ${ARCH}/node:${TAG_SUFFIX}-alpine

ARG TAG_VERSION
ARG TAG_SUFFIX
ARG HOST_USER_UID
ARG TIMEZONE

LABEL org.opencontainers.image.authors="Sindria Inc. <info@sindria.org>"

LABEL \
	name="node" \
	image="sindriainc/node" \
	tag="${TAG_VERSION}-${TAG_SUFFIX}" \
	vendor="sindria"

ENV TZ=${TIMEZONE}
ENV SINDRIA_USER="sindria"
ENV SINDRIA_USER_HOME="/home/sindria"

# Update and install packages
RUN apk update && \
    apk add --no-cache \
    bash \
    shadow \
    curl \
    screen \
    vim \
    wget \
    git \
    rsync \
    ca-certificates \
    tzdata && \
    apk upgrade

# Install yarn
RUN apk add yarn --no-cache

# Patch node UID
RUN usermod -u 1001 node && \
    groupmod -g 1001 node

# Setting app user, timezone and permissions
RUN addgroup -S ${SINDRIA_USER} -g ${HOST_USER_UID} && adduser -s /bin/bash -S ${SINDRIA_USER} -u ${HOST_USER_UID} -G ${SINDRIA_USER} -h ${SINDRIA_USER_HOME} && \
    mkdir -p /var/www && \
    mkdir -p /var/www/app && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && \
    chmod -R 775 /var/www/app && \
    chown -R ${SINDRIA_USER}:root /var/www/app && \
    mkdir -p /context && \
    chmod -R 755 /context && \
    chown -R ${SINDRIA_USER}:${SINDRIA_USER} /context

WORKDIR /var/www/app

SHELL ["/bin/bash", "-c"]