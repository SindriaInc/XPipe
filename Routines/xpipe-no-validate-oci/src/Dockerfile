ARG ARCH
FROM sindriainc/deployer:2.1.0-${ARCH}

WORKDIR /context

ARG TAG_VERSION
ARG HOST_USER_UID
ARG TIMEZONE

LABEL \
	name="xpipe-no-validate-oci" \
	image="sindriainc/xpipe-no-validate-oci" \
	tag="${TAG_VERSION}" \
	vendor="sindria"

ENV TZ=${TIMEZONE}
ENV SINDRIA_USER="sindria"
ENV SINDRIA_USER_HOME="/home/sindria"

# Install application
#COPY . /var/www/app

# Setting Timezone and Fixing permission
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && \
    chmod -R 770 /var/www/app && \
    chown -R ${SINDRIA_USER}:${SINDRIA_USER} /var/www/app && \
    chmod -R 775 /context && \
    chown -R ${SINDRIA_USER}:${SINDRIA_USER} /context && \
    git config --global --add safe.directory /context

# Add and setup entrypoint
COPY bin/entrypoint.sh /usr/local/bin/entrypoint.sh
ENTRYPOINT ["/bin/bash", "entrypoint.sh"]