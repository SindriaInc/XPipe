ARG ARCH
ARG TAG_SUFFIX
FROM --platform=linux/${ARCH} mysql:${TAG_SUFFIX}

ARG TAG_VERSION
ARG TAG_SUFFIX
ARG HOST_USER_UID
ARG TIMEZONE
ARG AWSCLI_ARCH

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
    curl \
    git \
    rsync \
    gcc \
    unzip \
    make && \
    microdnf clean all

# Install awscli
RUN cd /tmp && \
    curl "https://awscli.amazonaws.com/awscli-exe-linux-${AWSCLI_ARCH}.zip" -o "awscliv2.zip" && \
    unzip awscliv2.zip && \
    sudo ./aws/install && \
    rm -Rf awscliv2.zip && \
    rm -Rf aws && \
    rm -rf /usr/local/aws-cli/v2/*/dist/aws_completer && \
    rm -rf /usr/local/aws-cli/v2/*/dist/awscli/data/ac.index && \
    rm -rf /usr/local/aws-cli/v2/*/dist/awscli/examples && \
    /usr/local/bin/aws --version

USER root

# Add and setup entrypoint
COPY resources/bin/entrypoint.sh /usr/local/bin/entrypoint.sh
ENTRYPOINT ["/bin/bash", "entrypoint.sh"]

# Temporary disabled
#USER ${SINDRIA_USER}