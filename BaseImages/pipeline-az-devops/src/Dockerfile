ARG ARCH
FROM ${ARCH}/node:12-alpine

ARG TAG_VERSION
ARG HOST_USER_UID
ARG TIMEZONE
ARG KUBECTL_ARCH

LABEL org.opencontainers.image.authors="Sindria Inc. <info@sindria.org>"

LABEL \
	name="pipeline-az-devops" \
	image="sindriainc/pipeline-az-devops" \
	tag="${TAG_VERSION}" \
	vendor="sindria"

LABEL "com.azure.dev.pipelines.agent.handler.node.path"="/usr/local/bin/node"

ENV PATH "/root/.local/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"

# Make default staging cache
RUN mkdir -p /staging && \
    chmod 777 /staging && \
    chown -R 1001:1001 /staging

# Update and install packages
RUN apk update && \
    apk add \
    bash \
    sudo \
    shadow \
    curl \
    git \
    rsync && \
    rm -rf /var/cache/apk/*

# Add AZ dependencies
RUN apk add --no-cache --virtual .pipeline-deps readline linux-pam \
  && apk add bash sudo shadow \
  && apk del .pipeline-deps

# Setup docker
RUN echo "http://dl-cdn.alpinelinux.org/alpine/latest-stable/community" >> /etc/apk/repositories && \
    apk update && \
    apk add docker-cli && \
    rm -rf /var/cache/apk/*

# Install kubectl
RUN curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/${KUBECTL_ARCH}/kubectl" && \
    curl -LO "https://dl.k8s.io/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/${KUBECTL_ARCH}/kubectl.sha256" && \
    #echo "$(<kubectl.sha256) kubectl" | sha256sum --check && \
    install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl && \
    kubectl version --client

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

SHELL ["/bin/bash", "-c"]

CMD [ "node" ]

USER root