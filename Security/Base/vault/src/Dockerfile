ARG PLATFORM
ARG DIGEST
ARG ARCH
ARG TAG_SUFFIX
FROM --platform=$PLATFORM hashicorp/vault@$DIGEST

ARG TAG_VERSION
ARG TAG_SUFFIX
ARG HOST_USER_UID
ARG TIMEZONE

LABEL org.opencontainers.image.authors="Sindria Inc. <info@sindria.org>"

LABEL \
	name="vault" \
	image="sindriainc/vault" \
	tag="\${TAG_VERSION}-\${TAG_SUFFIX}" \
	vendor="sindria"

ENV TZ=${TIMEZONE}
ENV SINDRIA_USER="vault"
ENV SINDRIA_USER_HOME="/vault"

ENV VAULT_UI="true"
ENV VAULT_API_ADDRESS="https://0.0.0.0:8200"
ENV VAULT_DISABLE_MLOCK="true"
ENV VAULT_MYSQL_USERNAME="root"
ENV VAULT_MYSQL_PASSWORD="secret"
ENV VAULT_MYSQL_DATABASE="app"
ENV VAULT_MYSQL_ADDRESS="xpipe-vault-db.xpipe-sindria.svc.cluster.local"
ENV VAULT_MYSQL_PLAINTEXT_CONNECTION_ALLOWED="true"
ENV VAULT_TLS_DISABLE="true"

ENV VAULT_SELFSIGNED_CERT_SUBJ="/C=IT/ST=Italy/L=MI/O=MI/OU=MI/CN=*.sindria.org emailAddress=info@sindria.org"

# Install packages
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

# Install openssl
RUN apk add --no-cache openssl

# Setting timezone
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && \
    mkdir -p /var/run/vault && \
    chown -R vault:root /var/run/vault && \
    mkdir -p /vault/config && \
    mkdir -p /vault/bin && \
    chown -R vault:vault /vault/bin && \
    mkdir -p /vault/data && \
    chown -R vault:vault /vault/data

COPY resources/config.hcl /vault/config/config.hcl
COPY resources/post_start.sh /post_start.sh
COPY resources/gen_cert.sh /vault/bin/gen_cert.sh

WORKDIR /vault

COPY resources/entrypoint.sh /usr/local/bin/entrypoint.sh
ENTRYPOINT ["/bin/sh", "/usr/local/bin/entrypoint.sh"]