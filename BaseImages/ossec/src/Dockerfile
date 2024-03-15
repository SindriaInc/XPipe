# Extends base image fedora
ARG FEDORA_VERSION
FROM fedora:${FEDORA_VERSION}

ARG FEDORA_VERSION
ARG TAG_VERSION
ARG HOST_USER_UID
ARG TIMEZONE

LABEL org.opencontainers.image.authors="Sindria Inc. <info@sindria.org>"

LABEL \
	name="ossec" \
	image="sindriainc/ossec" \
	tag="${TAG_VERSION}" \
	vendor="sindria"

ENV FEDORA_VERSION ${FEDORA_VERSION}
ENV HOST_USER_UID ${HOST_USER_UID}
ENV SINDRIA_USER="sindria"
ENV SINDRIA_USER_HOME="/home/sindria"
ENV TZ=${TIMEZONE}

# Update system, install base software and set timezone
RUN dnf update -y && \
    rpm -ivh http://download1.rpmfusion.org/free/fedora/rpmfusion-free-release-${FEDORA_VERSION}.noarch.rpm && \
    dnf install curl wget postfix tmux screen rsync vim unzip git passwd procps iproute iputils telnet openssl htop net-tools hostname bzip2 -y && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && \
    dnf clean all

# Adding sindria user
RUN useradd ${SINDRIA_USER} -u ${HOST_USER_UID} -m -d ${SINDRIA_USER_HOME} -s /bin/bash && \
    groupmod ${SINDRIA_USER} -g ${HOST_USER_UID} && \
    chown -R ${SINDRIA_USER}:${SINDRIA_USER} ${SINDRIA_USER_HOME}

# Install Ossec
COPY root_fs/ /
RUN wget -q -O - https://updates.atomicorp.com/installers/atomic | bash && \
    dnf install -y ossec-hids-server && \
    dnf clean all && \
    chmod 755 /ossec-server.sh && \
	chmod 755 /init.sh && \
  	sync && /init.sh &&\
  	sync && rm /init.sh

VOLUME ["/var/ossec/data"]

ENTRYPOINT ["/ossec-server.sh"]

EXPOSE 1514/udp 1515/tcp