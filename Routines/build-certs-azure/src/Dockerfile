FROM certbot/certbot:latest

ARG TAG_VERSION
ARG TIMEZONE

LABEL \
	name="build-certs-azure" \
	image="sindriainc/build-certs-azure" \
	tag="${TAG_VERSION}" \
	vendor="sindria"

ENV PATH "/root/.local/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/home/sindria/.local/bin"
ENV TZ=${TIMEZONE}

USER root

# Setting timezone
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Update and install packages
RUN apk update && \
    apk add \
    bash \
    curl \
    git \
    rsync \
    python3 \
    python3-dev \
    py-pip \
    libffi-dev \
    openssl-dev \
    libressl-dev \
    musl-dev \
    rust \
    cargo \
    gcc \
    libc-dev \
    make \
    build-base && \
    pip3 install pip --upgrade && \
    apk add --no-cache libc6-compat && \
    rm -rf /var/cache/apk/*

# Install azure cli
RUN pip3 install urllib3 --upgrade --user && \
    pip3 install six --upgrade --user && \
    pip3 install --upgrade --user azure-cli

# Install certbot azure plugin
RUN pip install certbot-dns-azure

# Add and setup entrypoint
COPY bin/entrypoint.sh /usr/local/bin/entrypoint.sh
ENTRYPOINT ["/bin/bash", "entrypoint.sh"]