ARG ARCH
FROM certbot/dns-ovh:${ARCH}-v2.7.4

ARG TAG_VERSION
ARG TIMEZONE

LABEL \
	name="maintenance-v1-rtn-certsovh" \
	image="sindriainc/maintenance-v1-rtn-certsovh" \
	tag="${TAG_VERSION}" \
	vendor="sindria"

ENV AWSCLI_VERSION "1.16.268"
ENV PATH "/root/.local/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/home/sindria/.local/bin"
ENV TZ=${TIMEZONE}

USER root

# Setting timezone
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /var/www/app

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
    rm -rf /var/cache/apk/*

# Install awscli
RUN pip3 install urllib3 --upgrade --user && \
    pip3 install six --upgrade --user && \
    pip3 install awscli --upgrade --user

COPY resources/tls-template.yaml /var/www/app/resources/tls-template.yaml

# Add and setup entrypoint
COPY bin/entrypoint.sh /usr/local/bin/entrypoint.sh
ENTRYPOINT ["/bin/bash", "entrypoint.sh"]