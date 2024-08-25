#!/usr/bin/env bash

if [[ -z "$1" ]]; then
    echo "Provide image name as first argument (eg. sindriainc/<repo-slug>)"
    exit 1
fi

if [[ -z "$2" ]]; then
    echo "Provide a tag version as second argument (eg. 1.0.0)"
    exit 1
fi

if [[ -z "$3" ]]; then
    echo "Provide a tag suffix as third argument (eg. 1.12.0)"
    exit 1
fi

if [[ -z "$4" ]]; then
    echo "Provide a tag arch as fourth argument (eg. amd64, arm64)"
    exit 1
fi

if [[ -z "$5" ]]; then
    echo "Provide a tag platform as fifth argument (eg. linux/amd64, linux/arm64/v8)"
    exit 1
fi

if [[ -z "$6" ]]; then
    echo "Provide a tag digest as sixth argument (eg. sha256:09354ca0891f7cee8fbfe8db08c62d2d757fad8ae6c91f2b6cce7a34440e3fae)"
    exit 1
fi

IMAGE_NAME=$1
TAG_VERSION=$2
TAG_SUFFIX=$3
TAG_ARCH=$4
TAG_PLATFORM=$5
TAG_DIGEST=$6

HOST_USER_UID=1000
TIMEZONE=Europe/Rome

# Only for test build
if [ "${TAG_VERSION}" == "test" ]; then
    docker build ./test/src \
    --tag ${IMAGE_NAME}:${TAG_VERSION}-${TAG_SUFFIX}-${TAG_ARCH} \
    --tag ${IMAGE_NAME}:latest \
    --build-arg PLATFORM=${TAG_PLATFORM} \
    --build-arg DIGEST=${TAG_DIGEST} \
    --build-arg ARCH=${TAG_ARCH} \
    --build-arg TAG_VERSION=${TAG_VERSION} \
    --build-arg TAG_SUFFIX=${TAG_SUFFIX} \
    --build-arg HOST_USER_UID=${HOST_USER_UID} \
    --build-arg TIMEZONE=${TIMEZONE}
fi

if [ "${TAG_VERSION}" != "test" ]; then
    docker build ./src \
    --tag ${IMAGE_NAME}:${TAG_VERSION}-${TAG_SUFFIX}-${TAG_ARCH} \
    --tag ${IMAGE_NAME}:latest \
    --build-arg PLATFORM=${TAG_PLATFORM} \
    --build-arg DIGEST=${TAG_DIGEST} \
    --build-arg ARCH=${TAG_ARCH} \
    --build-arg TAG_VERSION=${TAG_VERSION} \
    --build-arg TAG_SUFFIX=${TAG_SUFFIX} \
    --build-arg HOST_USER_UID=${HOST_USER_UID} \
    --build-arg TIMEZONE=${TIMEZONE}
fi


#docker build --tag ${IMAGE_NAME}:${TAG_VERSION}-${TAG_SUFFIX}-${TAG_ARCH} --build-arg PLATFORM=${TAG_PLATFORM} --build-arg DIGEST=${TAG_DIGEST} --build-arg ARCH=${TAG_ARCH} --build-arg TAG_VERSION=${TAG_VERSION} --build-arg TAG_SUFFIX=${TAG_SUFFIX} --build-arg HOST_USER_UID=${HOST_USER_UID} --build-arg TIMEZONE=${TIMEZONE} -<<EOF
#ARG PLATFORM
#ARG DIGEST
#ARG ARCH
#ARG TAG_SUFFIX
#FROM --platform=\$PLATFORM hashicorp/vault@\$DIGEST
#
#ARG TAG_VERSION
#ARG TAG_SUFFIX
#ARG HOST_USER_UID
#ARG TIMEZONE
#
#LABEL org.opencontainers.image.authors="Sindria Inc. <info@sindria.org>"
#
#LABEL \
#	name="vault" \
#	image="sindriainc/vault" \
#	tag="\${TAG_VERSION}-\${TAG_SUFFIX}" \
#	vendor="sindria"
#
#ENV TZ=\${TIMEZONE}
#ENV SINDRIA_USER="vault"
#ENV SINDRIA_USER_HOME="/vault"
#
#ENV VAULT_UI="true"
#ENV VAULT_API_ADDRESS="https://0.0.0.0:8200"
#ENV VAULT_DISABLE_MLOCK="true"
#ENV VAULT_MYSQL_USERNAME="root"
#ENV VAULT_MYSQL_PASSWORD="secret"
#ENV VAULT_MYSQL_DATABASE="app"
#ENV VAULT_MYSQL_ADDRESS="xpipe-vault-db.xpipe-sindria.svc.cluster.local"
#ENV VAULT_MYSQL_PLAINTEXT_CONNECTION_ALLOWED="true"
#ENV VAULT_TLS_DISABLE="true"
#
#ENV VAULT_SELFSIGNED_CERT_SUBJ="/C=IT/ST=Italy/L=MI/O=MI/OU=MI/CN=*.sindria.org emailAddress=info@sindria.org"
#
## Install packages
#RUN apk update && \
#    apk add --no-cache \
#    gcc \
#    g++ \
#    libffi-dev \
#    libc-dev \
#    libressl-dev \
#    bash \
#    curl \
#    wget \
#    git \
#    rsync \
#    ca-certificates \
#    tzdata && \
#    apk upgrade
#
## Install openssl
#RUN apk add --no-cache openssl
#
## Setting timezone
#RUN ln -snf /usr/share/zoneinfo/\$TZ /etc/localtime && echo \$TZ > /etc/timezone && \
#    mkdir -p /var/run/vault && \
#    chown -R vault:root /var/run/vault && \
#    mkdir -p /vault/config && \
#    mkdir -p /vault/bin && \
#    chown -R vault:vault /vault/bin && \
#    mkdir -p /vault/data && \
#    chown -R vault:vault /vault/data
#
#COPY src/resources/config.hcl /vault/config.hcl
#COPY src/resources/post_start.sh /post_start.sh
#COPY src/resources/gen_cert.sh /vault/bin/gen_cert.sh
#
#WORKDIR /vault
#
#COPY resources/entrypoint.sh /usr/local/bin/entrypoint.sh
#ENTRYPOINT ["/bin/sh", "/usr/local/bin/entrypoint.sh"]
#EOF