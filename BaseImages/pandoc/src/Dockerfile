ARG ARCH
ARG FEDORA_VERSION
FROM ${ARCH}/fedora:${FEDORA_VERSION}

ARG FEDORA_VERSION
ARG TAG_VERSION
ARG TAG_SUFFIX
ARG HOST_USER_UID
ARG TIMEZONE

LABEL org.opencontainers.image.authors="Sindria Inc. <info@sindria.org>"

LABEL \
	name="pandoc" \
	image="sindriainc/pandoc" \
	tag="${TAG_VERSION}-${TAG_SUFFIX}" \
	vendor="sindria"

ENV FEDORA_VERSION=${FEDORA_VERSION}
ENV HOST_USER_UID=${HOST_USER_UID}
ENV TZ=${TIMEZONE}
ENV SINDRIA_USER="sindria"
ENV SINDRIA_USER_HOME="/home/sindria"

# Update and install pandoc
RUN dnf install -y texlive-scheme-basic pandoc && \
    dnf clean all

# Setting app user, timezone and permissions
RUN useradd ${SINDRIA_USER} -u ${HOST_USER_UID} -m -d ${SINDRIA_USER_HOME} -s /bin/bash && \
    groupmod ${SINDRIA_USER} -g ${HOST_USER_UID} && \
    mkdir -p /var/www && \
    mkdir -p /var/www/app && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && \
    chmod -R 770 /var/www/app && \
    chown -R ${SINDRIA_USER}:${SINDRIA_USER} /var/www/app && \
    mkdir -p /context && \
    chmod -R 755 /context && \
    chown -R ${SINDRIA_USER}:${SINDRIA_USER} /context

WORKDIR /var/www/app

SHELL ["/bin/bash", "-c"]

USER ${SINDRIA_USER}