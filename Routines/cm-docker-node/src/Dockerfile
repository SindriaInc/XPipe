FROM sindriainc/ansible:1.0.0-2.9.9

WORKDIR /var/www/app

ARG TAG_VERSION
ARG HOST_USER_UID
ARG TIMEZONE

LABEL \
	name="cm-docker-node" \
	image="sindriainc/cm-docker-node" \
	tag="${TAG_VERSION}" \
	vendor="sindria"

ENV AWSCLI_VERSION "1.16.268"
ENV PATH "/root/.local/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/home/sindria/.local/bin"
ENV TZ=${TIMEZONE}
ENV SINDRIA_USER="sindria"
ENV SINDRIA_USER_HOME="/home/sindria"

USER root

# Update and install packages
RUN apk update && \
    apk add \
    bash \
    openssh-client \
    curl \
    git \
    rsync \
    python3 \
    python3-dev \
    py-pip \
    libffi-dev \
    openssl-dev \
    gcc \
    libc-dev \
    make \
    build-base && \
    rm -rf /var/cache/apk/*

# Install awscli
RUN pip3 install urllib3 --upgrade --user && \
    pip3 install six --upgrade --user && \
    pip3 install awscli --upgrade --user

# Install application
COPY ./ /var/www/app

# Fixing permission
RUN chmod -R 770 /var/www/app && \
    chown -R ${SINDRIA_USER}:${SINDRIA_USER} /var/www/app

# Add and setup entrypoint
COPY bin/entrypoint.sh /usr/local/bin/entrypoint.sh
ENTRYPOINT ["/bin/bash", "entrypoint.sh"]

#USER ${SINDRIA_USER}