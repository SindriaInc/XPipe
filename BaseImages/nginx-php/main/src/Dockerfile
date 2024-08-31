ARG ARCH
ARG TAG_SUFFIX
FROM sindriainc/php:4.1.0-${TAG_SUFFIX}-${ARCH}

ARG TAG_VERSION
ARG TAG_SUFFIX
ARG HOST_USER_UID
ARG TIMEZONE

LABEL org.opencontainers.image.authors="Sindria Inc. <info@sindria.org>"

LABEL \
	name="sindriainc/nginx-php" \
	image="nginx-php" \
	tag="${TAG_VERSION}-${TAG_SUFFIX}" \
	vendor="sindria"

ENV HOST_USER_UID ${HOST_USER_UID}
ENV TZ=${TIMEZONE}
ENV BOOTSTRAP_USER="bootstrap"
ENV SINDRIA_USER="sindria"
ENV SINDRIA_USER_HOME="/home/sindria"
ENV PHP_VERSION=${TAG_SUFFIX}
ENV PHP_PM_MAX_CHILDREN=16

# Install packages
RUN apk add \
    sudo \
    supervisor \
    nginx && \
    rm -rf /var/cache/apk/*
    
COPY resources/sudo/sudoers /etc/sudoers

# Install fpm
COPY resources/php/fpm/pool.d/sindria.conf ${SINDRIA_USER_HOME}
COPY resources/php/install.sh ${SINDRIA_USER_HOME}
RUN bash ${SINDRIA_USER_HOME}/install.sh ${PHP_VERSION} && \
    rm -rf ${SINDRIA_USER_HOME}/install.sh && \
    rm -rf ${SINDRIA_USER_HOME}/sindria.conf && \
    rm -rf /var/cache/apk/*

# Nginx configuration
RUN mkdir -p /run/nginx && \
    chown -R ${SINDRIA_USER}:${SINDRIA_USER} /run/nginx && \
    rm -rf /etc/nginx/conf.d/default.conf && \
    mkdir -p /etc/nginx/sites-enabled && \
    mkdir -p /var/log/nginx/ && \
    chmod 755 /var/log/nginx/ && \
    touch /var/log/nginx/error.log && \
    touch /var/log/nginx/access.log && \
    chmod 755 /var/log/nginx/ && \
    chown -R ${SINDRIA_USER}:${SINDRIA_USER} /var/log/nginx/ && \
    chown ${SINDRIA_USER}:${SINDRIA_USER} /var/log/nginx/error.log && \
    chown ${SINDRIA_USER}:${SINDRIA_USER} /var/log/nginx/access.log && \
    mkdir -p /var/lib/nginx && \
    chown -R ${SINDRIA_USER}:${SINDRIA_USER} /var/lib/nginx && \
    rm /var/lib/nginx/logs && \
    mkdir -p /var/lib/nginx/logs && \
    chown -R ${SINDRIA_USER}:${SINDRIA_USER} /var/lib/nginx/logs && \
    touch /var/lib/nginx/logs/error.log && \
    touch /var/lib/nginx/logs/access.log && \
    chown ${SINDRIA_USER}:${SINDRIA_USER} /var/lib/nginx/logs/error.log && \
    chown ${SINDRIA_USER}:${SINDRIA_USER} /var/lib/nginx/logs/access.log && \
    mkdir -p /var/lib/nginx/tmp/ && \
    chmod 755 /var/lib/nginx/tmp/ && \
    chown -R ${SINDRIA_USER}:${SINDRIA_USER} /var/lib/nginx/tmp/

COPY resources/nginx/nginx.conf /etc/nginx/
COPY resources/nginx/conf.d/*.conf /etc/nginx/conf.d/
COPY resources/nginx/sites-available/app.conf /etc/nginx/sites-enabled/

# forward request and error logs to docker log collector
RUN ln -sf /dev/stdout /var/log/nginx/access.log && \
    ln -sf /dev/stderr /var/log/nginx/error.log

# Add and execute startup command
COPY resources/bin/startup.sh /usr/local/bin/startup.sh

# Supervisor configuration
COPY resources/supervisor/supervisord.conf /etc/supervisor/supervisord.conf
COPY resources/supervisor/conf.d/*.conf /etc/supervisor/conf.d/
RUN mkdir -p /var/run/supervisor && \
    chmod 755 /var/run/supervisor && \
    chown -R ${SINDRIA_USER}:root /var/run/supervisor && \
    mkdir -p /var/log/supervisor && \
    touch /var/log/supervisor/supervisord.log && \
    chmod 744 /var/log/supervisor/supervisord.log && \
    chown -R ${SINDRIA_USER}:${SINDRIA_USER} /var/log/supervisor && \
    usermod -a -G wheel ${SINDRIA_USER} && \
    usermod -a -G wheel ${BOOTSTRAP_USER} && \
    chmod +xs /usr/local/bin/startup.sh

USER ${SINDRIA_USER}

CMD ["sudo", "--preserve-env", "su", "bootstrap", "-c", "/usr/local/bin/startup.sh"]

ENTRYPOINT [""]

EXPOSE 80

# Configure a healthcheck to validate that everything is up&running
#HEALTHCHECK --timeout=10s CMD curl --silent --fail http://127.0.0.1:80/fpm-ping