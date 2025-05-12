ARG ARCH
FROM ${ARCH}/alpine:3

ARG TAG_VERSION
ARG HOST_USER_UID
ARG TIMEZONE

LABEL org.opencontainers.image.authors="Sindria Inc. <info@sindria.org>"

LABEL \
	name="pipeline" \
	image="sindriainc/pipeline" \
	tag="${TAG_VERSION}" \
	vendor="sindria"

ENV PATH "/root/.local/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"

# Update and install packages
RUN apk update && \
    apk add \
    bash \
    curl \
    git \
    rsync && \
    rm -rf /var/cache/apk/*

# Setup docker
RUN echo "http://dl-cdn.alpinelinux.org/alpine/latest-stable/community" >> /etc/apk/repositories && \
    apk update && \
    apk add docker-cli && \
    rm -rf /var/cache/apk/*

# Install cowsay
RUN apk update \
    && apk add perl \
    && cd /tmp/ \
    && git clone https://github.com/jasonm23/cowsay.git \
    && cd cowsay && ./install.sh /usr/local \
    && cd .. \
    && rm -rf cowsay

COPY resources/cowsay/docker.cow /usr/local/share/cows/
COPY resources/cowsay/pikachu.cow /usr/local/share/cows/

# Move the "default.cow" out of the way so we can overwrite it with "docker.cow"
RUN \
    mv /usr/local/share/cows/default.cow /usr/local/share/cows/orig-default.cow \
    && ln -sv /usr/local/share/cows/docker.cow /usr/local/share/cows/default.cow

SHELL ["/bin/bash", "-c"]