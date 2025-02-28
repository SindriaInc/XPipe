# Build Stage
ARG ARCH
FROM sindriainc/nginx-php:6.0.0-local-7.4-${ARCH} as builder

USER root

# Add source code
COPY ./ /var/www/app

WORKDIR /var/www/app

# Setup app env
RUN cp .env.production .env

# Build dependecies
RUN composer install --no-interaction --no-suggest --no-ansi --no-progress

# Build Artifact
# TODO: build phar artifact

# Push Artifact
# TODO: push phar artifact

# Production Stage
ARG ARCH
FROM sindriainc/nginx-php:6.0.0-local-7.4-${ARCH}

WORKDIR /var/www/app

ARG ARCH
ARG TAG_VERSION
ARG HOST_USER_UID
ARG TIMEZONE

LABEL \
	name="xpipe-cmf" \
	image="sindriaproject/xpipe-cmf" \
	tag="${TAG_VERSION}" \
	vendor="sindria"

ENV DEBIAN_FRONTEND="noninteractive"
ENV TZ=${TIMEZONE}
ENV SINDRIA_USER="sindria"
ENV SINDRIA_USER_HOME="/home/sindria"

USER root

# Install application
COPY --from=builder /var/www/app /var/www/app

# Setting Timezone and Fixing permission
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && \
    chmod -R 770 /var/www/app && \
    chown -R ${SINDRIA_USER}:${SINDRIA_USER} /var/www/app && \
    chmod +x /usr/local/bin/startup.sh && \
    chmod +x /var/www/app/bin/run-scheduler.sh && \
    chmod +x /var/www/app/bin/cmd.sh

CMD ["/bin/bash", "/var/www/app/bin/cmd.sh"]

USER ${SINDRIA_USER}
