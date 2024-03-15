ARG ARCH
ARG TAG_SUFFIX
FROM ${ARCH}/golang:${TAG_SUFFIX}-alpine

ARG TAG_VERSION
ARG HOST_USER_UID
ARG TIMEZONE

LABEL org.opencontainers.image.authors="Sindria Inc. <info@sindria.org>"

LABEL \
	name="golang" \
	image="sindriainc/golang" \
	tag="${TAG_VERSION}-${TAG_SUFFIX}" \
	vendor="sindria"

ENV TZ=${TIMEZONE}
ENV SINDRIA_USER="sindria"
ENV SINDRIA_USER_HOME="/home/sindria"

# Update and install packages
RUN apk update && \
    apk add --no-cache \
    gcc \
    g++ \
    libffi-dev \
    libc-dev \
    libressl-dev \
    bash \
    curl \
    wget \
    git \
    rsync \
    ca-certificates \
    tzdata && \
    apk upgrade

# Setting app user, timezone and permissions
RUN addgroup -S ${SINDRIA_USER} -g ${HOST_USER_UID} && adduser -s /bin/bash -S ${SINDRIA_USER} -u ${HOST_USER_UID} -G ${SINDRIA_USER} -h ${SINDRIA_USER_HOME} && \
    mkdir -p /var/www && \
    mkdir -p /var/www/app && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && \
    chmod -R 770 /var/www/app && \
    chown -R ${SINDRIA_USER}:${SINDRIA_USER} /var/www/app && \
    mkdir -p /context && \
    chmod -R 755 /context && \
    chown -R ${SINDRIA_USER}:${SINDRIA_USER} /context

WORKDIR /var/www/app

SHELL ["/bin/bash", "-c"]