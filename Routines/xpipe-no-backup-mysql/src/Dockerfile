ARG ARCH
ARG TAG_SUFFIX
FROM ${ARCH}/mysql:${TAG_SUFFIX}

ARG TAG_VERSION
ARG TAG_SUFFIX
ARG HOST_USER_UID
ARG TIMEZONE

LABEL org.opencontainers.image.authors="Sindria Inc. <info@sindria.org>"

LABEL \
	name="xpipe-no-backup-mysql" \
	image="sindriainc/xpipe-no-backup-mysql" \
	tag="${TAG_VERSION}" \
	vendor="sindria"

ENV AWSCLI_VERSION "1.16.268"
ENV PATH "/root/.local/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/home/sindria/.local/bin"
ENV TZ=${TIMEZONE}
ENV SINDRIA_USER="mysql"

# Setting app user, timezone and permissions
RUN mkdir -p /var/www && \
    mkdir -p /var/www/app && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && \
    chmod -R 770 /var/www/app && \
    chown -R ${SINDRIA_USER}:${SINDRIA_USER} /var/www/app

WORKDIR /var/www/app

# Update packages
RUN microdnf upgrade \
  --refresh \
  --best \
  --nodocs \
  --noplugins \
  --setopt=install_weak_deps=0

# Install packages
RUN microdnf install -y \
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
    microdnf clean all

# Install awscli
RUN pip3 install urllib3 --upgrade --user && \
    pip3 install six --upgrade --user && \
    pip3 install awscli --upgrade --user

USER root

# Add and setup entrypoint
COPY resources/bin/entrypoint.sh /usr/local/bin/entrypoint.sh
ENTRYPOINT ["/bin/bash", "entrypoint.sh"]

# Temporary disabled
#USER ${SINDRIA_USER}