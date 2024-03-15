ARG ARCH
FROM ${ARCH}/fedora:39

ARG TAG_VERSION
ARG HOST_USER_UID
ARG TIMEZONE
ARG KUBECTL_ARCH
ARG AWSCLI_ARCH

LABEL org.opencontainers.image.authors="Sindria Inc. <info@sindria.org>"

LABEL \
	name="deployer" \
	image="sindriainc/deployer" \
	tag="${TAG_VERSION}" \
	vendor="sindria"

USER root

#ENV AWSCLI_VERSION "1.16.268"
ENV FEDORA_VERSION=39
ENV PATH "/root/.local/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/home/sindria/.local/bin"
ENV TZ=${TIMEZONE}
ENV SINDRIA_USER="sindria"
ENV SINDRIA_USER_HOME="/home/sindria"

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

# Update system, install base software and set timezone
RUN rpm -ivh http://download1.rpmfusion.org/free/fedora/rpmfusion-free-release-${FEDORA_VERSION}.noarch.rpm && \
    dnf install -y bash openssh-clients curl wget rsync vim unzip git passwd procps iputils telnet openssl htop net-tools hostname bzip2 && \
    dnf clean all

# Install docker and docker-compose
RUN dnf -y install dnf-plugins-core && \
    dnf config-manager --add-repo https://download.docker.com/linux/fedora/docker-ce.repo && \
    dnf install docker-ce docker-ce-cli containerd.io -y && \
    usermod -a -G docker ${SINDRIA_USER} && \
    groupmod -g 975 docker && \
    curl -L "https://github.com/docker/compose/releases/download/${DOCKER_COMPOSE_VERSION}/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose && \
    chmod +x /usr/local/bin/docker-compose && \
    dnf clean all

# Install kubectl
RUN curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/${KUBECTL_ARCH}/kubectl" && \
    curl -LO "https://dl.k8s.io/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/${KUBECTL_ARCH}/kubectl.sha256" && \
    #echo "$(<kubectl.sha256) kubectl" | sha256sum --check && \
    install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl && \
    kubectl version --client

# Install Helm
RUN curl -fsSL -o get_helm.sh https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 && \
    chmod 700 get_helm.sh && \
    ./get_helm.sh && \
    helm version && \
    rm -f get_helm.sh

# Install skopeo
RUN dnf -y install skopeo && \
    dnf clean all

WORKDIR /var/www/app

## Install oc
#COPY resources/oc.tar /var/www/app
#RUN apk add --no-cache gcompat && \
#    tar xvf oc.tar && \
#    mv oc /usr/local/bin/oc && \
#    chmod +x /usr/local/bin/oc && \
#    rm -Rf oc.tar

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

# Setup ssh
COPY resources/ssh/ /root/.ssh/
COPY resources/ssh/ ${SINDRIA_USER_HOME}/.ssh/
RUN mkdir -p /root/.ssh && \
    chmod 700 /root/.ssh && \
    chmod 600 /root/.ssh/* && \
    mkdir -p ${SINDRIA_USER_HOME}/.ssh && \
    chmod 700 ${SINDRIA_USER_HOME}/.ssh && \
    chmod 600 ${SINDRIA_USER_HOME}/.ssh/*

SHELL ["/bin/bash", "-c"]