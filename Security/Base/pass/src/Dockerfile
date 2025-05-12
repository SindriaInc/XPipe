ARG ARCH
FROM sindriainc/c-language:1.0.0-${ARCH}

ARG TAG_VERSION
ARG HOST_USER_UID
ARG TIMEZONE

LABEL org.opencontainers.image.authors="Sindria Inc. <info@sindria.org>"

LABEL \
	name="pass" \
	image="sindriainc/pass" \
	tag="${TAG_VERSION}" \
	vendor="sindria"

ENV TZ=${TIMEZONE}
ENV SINDRIA_USER="sindria"
ENV SINDRIA_USER_HOME="/home/sindria"

ENV PASSWORD_STORE_DIR="/var/www/app/data"
ENV PASSWORD_STORE_CLIP_TIME=60

# Update and install packages
RUN apk update && \
    apk add --no-cache \
    pass && \
    apk upgrade

# Setup pass
RUN mkdir -p /var/www/app/data && \
    chmod -R 770 /var/www/app && \
    chown -R ${SINDRIA_USER}:${SINDRIA_USER} /var/www/app && \
    chmod -R 700 /var/www/app/data

WORKDIR /var/www/app