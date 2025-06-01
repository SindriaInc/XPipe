# Extends base image fedora
ARG ARCH
ARG FEDORA_VERSION
FROM ${ARCH}/fedora:${FEDORA_VERSION}

ARG ARCH
ARG FEDORA_VERSION
ARG TAG_VERSION
ARG HOST_USER_UID
ARG HOST_DOCKER_GROUP_UID
ARG TIMEZONE
ARG XDEV_SINDRIA_USER_PASSWORD
ARG KUBECTL_ARCH
ARG AWSCLI_ARCH

LABEL org.opencontainers.image.authors="Sindria Inc. <info@sindria.org>"

LABEL \
	name="xdev" \
	image="sindriainc/xdev" \
	tag="${TAG_VERSION}" \
	vendor="sindria"

ENV FEDORA_VERSION ${FEDORA_VERSION}
ENV PATH "/root/.local/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/home/sindria/.local/bin"
ENV HOST_USER_UID ${HOST_USER_UID}
ENV SINDRIA_USER sindria
ENV SINDRIA_USER_HOME /home/sindria
ENV TZ ${TIMEZONE}
ENV SUDO_GROUP wheel
ENV XDEV_SINDRIA_USER_PASSWORD ${XDEV_SINDRIA_USER_PASSWORD}
ENV DOCKER_COMPOSE_VERSION 1.29.2
#ENV AWSCLI_VERSION "1.16.268"

# Update system, install base software and set timezone
RUN dnf update -y && \
    rpm -ivh http://download1.rpmfusion.org/free/fedora/rpmfusion-free-release-${FEDORA_VERSION}.noarch.rpm && \
    dnf install curl wget tmux screen rsync vim unzip supervisor git sudo zsh passwd procps iputils telnet openssl htop net-tools hostname openvpn bzip2 -y && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && \
    dnf clean all

COPY resources/etc/sudoers /etc/sudoers

# Install ssh server
RUN dnf install -y openssh-server && \
    ssh-keygen -A && \
    dnf clean all
COPY resources/ssh/sshd_config /etc/ssh/sshd_config

# Adding sindria user
RUN useradd ${SINDRIA_USER} -u ${HOST_USER_UID} -m -d ${SINDRIA_USER_HOME} -s /bin/zsh && \
    groupmod ${SINDRIA_USER} -g ${HOST_USER_UID} && \
    mkdir -p ${SINDRIA_USER_HOME}/config && \
    mkdir -p ${SINDRIA_USER_HOME}/.ssh && \
    chmod 700 ${SINDRIA_USER_HOME}/.ssh && \
    mkdir -p ${SINDRIA_USER_HOME}/.aws && \
    chmod 700 ${SINDRIA_USER_HOME}/.aws && \
    mkdir -p ${SINDRIA_USER_HOME}/.docker && \
    chmod 700 ${SINDRIA_USER_HOME}/.docker && \
    chown -R ${SINDRIA_USER}:${SINDRIA_USER} ${SINDRIA_USER_HOME} && \
    usermod -a -G ${SUDO_GROUP} ${SINDRIA_USER} && \
    echo "${SINDRIA_USER}:${XDEV_SINDRIA_USER_PASSWORD}" | chpasswd && \
    chmod 644 /etc/sudoers

# Install Oh-My-ZSH
RUN su sindria -c "cd ${SINDRIA_USER_HOME} && wget --no-check-certificate http://install.ohmyz.sh -O - | sh"

# Install docker and docker-compose
RUN dnf -y install dnf-plugins-core && \
    dnf config-manager --add-repo https://download.docker.com/linux/fedora/docker-ce.repo && \
    dnf install docker-ce docker-ce-cli containerd.io -y && \
    usermod -a -G docker ${SINDRIA_USER} && \
    groupmod -g ${HOST_DOCKER_GROUP_UID} docker && \
    curl -L "https://github.com/docker/compose/releases/download/${DOCKER_COMPOSE_VERSION}/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose && \
    sudo chmod +x /usr/local/bin/docker-compose && \
    dnf clean all

# Install kubectl
RUN curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/${KUBECTL_ARCH}/kubectl" && \
    curl -LO "https://dl.k8s.io/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/${KUBECTL_ARCH}/kubectl.sha256" && \
    #echo "$(<kubectl.sha256) kubectl" | sha256sum --check && \
    install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl && \
    kubectl version --client

## Install oc
#COPY resources/oc.tar /var/www/app
#RUN apk add --no-cache gcompat && \
#    tar xvf oc.tar && \
#    mv oc /usr/local/bin/oc && \
#    chmod +x /usr/local/bin/oc && \
#    rm -Rf oc.tar

# Install Helm
RUN curl -fsSL -o get_helm.sh https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 && \
    chmod 700 get_helm.sh && \
    ./get_helm.sh && \
    helm version && \
    rm -f get_helm.sh

# Install skopeo
RUN dnf -y install skopeo && \
    dnf clean all

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

# Reset permission
RUN chown -R ${SINDRIA_USER}:${SINDRIA_USER} ${SINDRIA_USER_HOME}

SHELL ["/bin/bash", "-c"]