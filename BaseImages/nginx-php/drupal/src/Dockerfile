# Extends nginx-php base image
ARG ARCH
ARG TAG_SUFFIX
FROM sindriainc/nginx-php:6.0.0-${TAG_SUFFIX}-${ARCH}

ARG TAG_VERSION
ARG TAG_SUFFIX
ARG HOST_USER_UID
ARG TIMEZONE

LABEL \
	name="sindriainc/nginx-php" \
	image="nginx-php" \
	tag="${TAG_VERSION}-${TAG_SUFFIX}" \
	vendor="sindria"

ENV HOST_USER_UID ${HOST_USER_UID}
ENV TZ=${TIMEZONE}
ENV SINDRIA_USER="sindria"
ENV SINDRIA_USER_HOME="/home/sindria"

WORKDIR /var/www/app

USER root

# Timezone configuration
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Nginx configuration
COPY resources/nginx/sites-available/app.conf /etc/nginx/sites-enabled/app.conf

# Drupal configuration
#COPY resources/composer/auth.json /root/.composer/auth.json
#COPY resources/composer/auth.json ${SINDRIA_USER_HOME}/.composer/auth.json
#RUN composer create-project drupal/recommended-project:9.1.3 . && \
#    chown -R ${SINDRIA_USER}:${SINDRIA_USER} ${SINDRIA_USER_HOME} && \
#    chown -R ${SINDRIA_USER}:${SINDRIA_USER} /var/www/app

USER ${SINDRIA_USER}