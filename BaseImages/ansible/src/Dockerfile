ARG ARCH
FROM sindriainc/python:3.0.0-3.8-${ARCH}

ARG TAG_VERSION
ARG TAG_SUFFIX
ARG HOST_USER_UID
ARG TIMEZONE

LABEL \
	name="ansible" \
	image="sindriainc/ansible" \
	tag="${TAG_VERSION}" \
	vendor="sindria"

ENV AWSCLI_VERSION "1.16.268"
ENV PATH "/root/.local/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/home/sindria/.local/bin"
ENV TZ=${TIMEZONE}
ENV SINDRIA_USER="sindria"
ENV SINDRIA_USER_HOME="/home/sindria"
ENV ANSIBLE_VERSION=${TAG_SUFFIX}
ENV MOLECULE_EXTRAS="docker,docs"
ENV MOLECULE_VERSION=3.0.2
ENV TESTINFRA_VERSION=5.1.0
ENV CRYPTOGRAPHY_DONT_BUILD_RUST=1

WORKDIR /var/www/app

# Update and install packages
RUN apk update && \
    pip install --upgrade pip && \
    apk add \
    git \
  	openssh-client && \
  	apk add --no-cache --upgrade --virtual .build-deps gcc make libressl-dev python3-dev libffi-dev musl-dev openssl-dev && \
    pip --no-cache-dir install \
        cryptography==2.1.4 \
		ansible \
		ansible-lint \
		flake8 \
        "molecule[$MOLECULE_EXTRAS]==$MOLECULE_VERSION" \
        testinfra==$TESTINFRA_VERSION \
	&& apk del .build-deps

# Temporary disabled
#USER ${SINDRIA_USER}

# Install awscli
RUN pip3 install urllib3 --upgrade --user && \
    pip3 install six --upgrade --user && \
    pip3 install awscli --upgrade --user

USER root

# Setup docker
RUN echo "http://dl-cdn.alpinelinux.org/alpine/latest-stable/community" >> /etc/apk/repositories && \
    apk update && \
    apk add docker docker-compose && \
    rm -rf /var/cache/apk/*

# Install cowsay
RUN apk update \
    && apk add perl \
    && cd /tmp/ \
    && git clone https://github.com/jasonm23/cowsay.git \
    && cd cowsay && ./install.sh /usr/local \
    && cd .. \
    && rm -rf cowsay

COPY resources/cowsay/docker.cow /usr/local/share/cows/
COPY resources/cowsay/pikachu.cow /usr/local/share/cows/

# Move the "default.cow" out of the way so we can overwrite it with "docker.cow"
RUN \
    mv /usr/local/share/cows/default.cow /usr/local/share/cows/orig-default.cow \
    && ln -sv /usr/local/share/cows/docker.cow /usr/local/share/cows/default.cow

# Setup ssh
COPY resources/ssh/ /root/.ssh/
RUN mkdir -p /root/.ssh && \
    chmod 700 /root/.ssh && \
    chmod 600 /root/.ssh/*

# Temporary disabled
#USER $SINDRIA_USER

SHELL ["/bin/bash", "-c"]