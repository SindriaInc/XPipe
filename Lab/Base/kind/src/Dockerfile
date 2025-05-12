ARG ARCH
FROM ${ARCH}/alpine:3

ARG TAG_VERSION
ARG HOST_USER_UID
ARG TIMEZONE
ARG KUBECTL_KIND_ARCH

LABEL org.opencontainers.image.authors="Sindria Inc. <info@sindria.org>"

LABEL \
	name="kind" \
	image="sindriainc/kind" \
	tag="${TAG_VERSION}" \
	vendor="sindria"

USER root

ENV PATH "/root/.local/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/home/sindria/.local/bin"
ENV TZ=${TIMEZONE}
ENV SINDRIA_USER="sindria"
ENV SINDRIA_USER_HOME="/home/sindria"

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

# Update and install packages
RUN apk update && \
    apk add \
    shadow \
    bash \
    openssh-client \
    curl \
    wget \
    vim \
    git \
    rsync \
    libffi-dev \
    openssl-dev \
    gcc \
    libc-dev \
    make \
    build-base \
    openssl \
    ca-certificates && \
    rm -rf /var/cache/apk/*

# Install docker-cli and docker-compose
RUN echo "http://dl-cdn.alpinelinux.org/alpine/latest-stable/community" >> /etc/apk/repositories && \
    apk update && \
    apk add docker-cli docker-compose && \
    rm -rf /var/cache/apk/*

# Install kubectl
RUN curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/${KUBECTL_KIND_ARCH}/kubectl" && \
    curl -LO "https://dl.k8s.io/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/${KUBECTL_KIND_ARCH}/kubectl.sha256" && \
    #echo "$(<kubectl.sha256) kubectl" | sha256sum --check && \
    install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl && \
    kubectl version --client

# Install Kind
RUN curl -Lo ./kind https://kind.sigs.k8s.io/dl/v0.11.1/kind-linux-${KUBECTL_KIND_ARCH} && \
    chmod +x ./kind && \
    mv ./kind /usr/bin/kind

WORKDIR /var/www/app

SHELL ["/bin/bash", "-c"]