FROM hashicorp/terraform:latest

ARG TAG_VERSION
ARG HOST_USER_UID
ARG TIMEZONE

LABEL \
	name="terraform" \
	image="sindriainc/terraform" \
	tag="${TAG_VERSION}" \
	vendor="sindria"

ENV AWSCLI_VERSION "1.16.268"
ENV PATH "/root/.local/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/home/sindria/.local/bin"
ENV TZ=${TIMEZONE}
ENV SINDRIA_USER="sindria"
ENV SINDRIA_USER_HOME="/home/sindria"

# Setting app user, timezone and permissions
RUN addgroup -S ${SINDRIA_USER} -g ${HOST_USER_UID} && adduser -s /bin/bash -S ${SINDRIA_USER} -u ${HOST_USER_UID} -G ${SINDRIA_USER} -h ${SINDRIA_USER_HOME} && \
    mkdir -p /var/www && \
    mkdir -p /var/www/app && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && \
    chmod -R 770 /var/www/app && \
    chown -R ${SINDRIA_USER}:${SINDRIA_USER} /var/www/app

WORKDIR /var/www/app

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

USER ${SINDRIA_USER}

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
COPY resources/ssh/ ${SINDRIA_USER_HOME}/.ssh/
RUN mkdir -p ${SINDRIA_USER_HOME}/.ssh && \
    chmod 700 ${SINDRIA_USER_HOME}/.ssh && \
    chmod 600 ${SINDRIA_USER_HOME}/.ssh/*

SHELL ["/bin/bash", "-c"]

ENTRYPOINT [""]

USER ${SINDRIA_USER}