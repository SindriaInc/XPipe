# Build Stage
FROM sindriainc/nginx-php:local-magento-7.3 as builder

ENV DEBIAN_FRONTEND="noninteractive"
ENV TZ=${TIMEZONE}
ENV SINDRIA_USER="sindria"
ENV SINDRIA_USER_HOME="/home/sindria"

# Add source code
COPY ./ /var/www/app

# Setting composer auth
RUN cp auth.json ~/.composer/auth.json && \
    chmod 600 ~/.composer/auth.json && \
    chown root:root ~/.composer/auth.json && \
    mkdir -p ${SINDRIA_USER_HOME}/.composer && \
    chmod 700 ${SINDRIA_USER_HOME}/.composer && \
    chown ${SINDRIA_USER}:${SINDRIA_USER} ${SINDRIA_USER_HOME}/.composer && \
    cp auth.json ${SINDRIA_USER_HOME}/.composer/auth.json && \
    chmod 600 ${SINDRIA_USER_HOME}/.composer/auth.json && \
    chown ${SINDRIA_USER}:${SINDRIA_USER} ${SINDRIA_USER_HOME}/.composer/auth.json

# Build dependecies
RUN composer install --no-interaction --no-suggest --no-ansi --no-progress

# Build app
#RUN php -dmemory_limit=6G bin/magento setup:di:compile

# Build Artifact
# TODO: build phar artifact

# Push Artifact
# TODO: push phar artifact

# Production Stage
FROM sindriainc/nginx-php:magento-7.3
#FROM sindriainc/nginx-php:local-magento-7.3

WORKDIR /var/www/app

ARG TAG_VERSION
ARG HOST_USER_UID
ARG TIMEZONE

LABEL \
	name="xp-ecommerce" \
	image="sindriaproject/xp-ecommerce" \
	tag="${TAG_VERSION}" \
	vendor="sindria"

ENV DEBIAN_FRONTEND="noninteractive"
ENV TZ=${TIMEZONE}
ENV SINDRIA_USER="sindria"
ENV SINDRIA_USER_HOME="/home/sindria"

# Install application
COPY --from=builder /var/www/app /var/www/app

# Setting Timezone and Fixing permission
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && \
    find . -type f -exec chmod 664 {} \; && \
    find . -type d -exec chmod 775 {} \; && \
    find var pub/static pub/media app/etc -type f -exec chmod g+w {} \; && \
    find var pub/static pub/media app/etc -type d -exec chmod g+ws {} \; && \
    chmod u+x bin/magento && \
    chown -R ${SINDRIA_USER}:${SINDRIA_USER} /var/www/app
