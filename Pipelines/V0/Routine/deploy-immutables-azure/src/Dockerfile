FROM sindriainc/python:1.2.2-3.8

WORKDIR /var/www/app

ARG TAG_VERSION
ARG HOST_USER_UID
ARG TIMEZONE

LABEL \
	name="deploy-immutables-azure" \
	image="sindriainc/deploy-immutables-azure" \
	tag="${TAG_VERSION}" \
	vendor="sindria"

ENV PATH "/root/.local/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/home/sindria/.local/bin"
ENV TZ=${TIMEZONE}
ENV SINDRIA_USER="sindria"
ENV SINDRIA_USER_HOME="/home/sindria"

USER root

# Setting timezone
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Update and install packages
RUN apk update && \
    apk add \
    bash \
    curl \
    git \
    rsync \
    python3 \
    python3-dev \
    py-pip \
    libffi-dev \
    openssl-dev \
    libressl-dev \
    musl-dev \
    rust \
    cargo \
    gcc \
    libc-dev \
    make \
    build-base && \
    pip3 install pip --upgrade && \
    apk add --no-cache libc6-compat && \
    rm -rf /var/cache/apk/*

USER ${SINDRIA_USER}

# Install azure cli
RUN pip3 install urllib3 --upgrade --user && \
    pip3 install six --upgrade --user && \
    pip3 install --upgrade --user azure-cli

USER root

## Install PowerShell
#RUN apk add --no-cache \
#    ca-certificates \
#    less \
#    ncurses-terminfo-base \
#    krb5-libs \
#    libgcc \
#    libintl \
#    libssl1.1 \
#    libstdc++ \
#    tzdata \
#    userspace-rcu \
#    zlib \
#    icu-libs \
#    curl && \
#    apk -X https://dl-cdn.alpinelinux.org/alpine/edge/main add --no-cache lttng-ust && \
#    curl -L https://github.com/PowerShell/PowerShell/releases/download/v7.1.4/powershell-7.1.4-linux-alpine-x64.tar.gz -o /tmp/powershell.tar.gz && \
#    mkdir -p /opt/microsoft/powershell/7 && \
#    tar zxf /tmp/powershell.tar.gz -C /opt/microsoft/powershell/7 && \
#    chmod +x /opt/microsoft/powershell/7/pwsh && \
#    ln -s /opt/microsoft/powershell/7/pwsh /usr/bin/pwsh
#
## Install Azure PowerShell and Azure RM PowerShell
#COPY resources/scripts/azure.ps1 /usr/local/bin/azure.ps1
#RUN pwsh /usr/local/bin/azure.ps1

# Install application
COPY ./ /var/www/app
RUN pip install -r requirements.txt && \
    rm -f /var/www/app/requirements.txt && \
    ln -s /var/www/app/app/main.py /usr/local/bin/app && \
    chmod -R 771 /var/www/app && \
    chown -R ${SINDRIA_USER}:${SINDRIA_USER} /var/www/app

# Add and setup entrypoint
COPY bin/entrypoint.sh /usr/local/bin/entrypoint.sh
ENTRYPOINT ["/bin/bash", "entrypoint.sh"]

USER ${SINDRIA_USER}