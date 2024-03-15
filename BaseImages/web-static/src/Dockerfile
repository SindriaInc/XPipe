FROM alpine:latest

ARG TAG_VERSION
ARG HOST_USER_UID
ARG TIMEZONE

LABEL \
	name="web-static" \
	image="sindriainc/web-static" \
	tag="${TAG_VERSION}" \
	vendor="sindria"

ENV PATH="/root/.local/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin" \
    TZ=${TIMEZONE} \
	SINDRIA_USER="sindria" \
	SINDRIA_USER_HOME="/home/sindria"

RUN apk update

# Install packages and setting
RUN apk add \
    bash \
    nginx \
    tzdata && \
    rm -rf /var/cache/apk/*

# Setting app user, timezone and permissions
RUN addgroup -S ${SINDRIA_USER} -g ${HOST_USER_UID} && adduser -S ${SINDRIA_USER} -u ${HOST_USER_UID} -G ${SINDRIA_USER} -h ${SINDRIA_USER_HOME} && \
    mkdir -p /var/www && \
    mkdir -p /var/www/app && \
    mkdir -p /var/www/app/public && \
    mkdir -p /run/nginx && \
    rm -rf /etc/nginx/conf.d/default.conf && \
    mkdir -p /etc/nginx/sites-enabled && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && \
    chmod -R 770 /var/www/app && \
    chown -R ${SINDRIA_USER}:${SINDRIA_USER} /var/www/app

# Nginx configuration
COPY resources/nginx/nginx.conf /etc/nginx/
COPY resources/nginx/conf.d/*.conf /etc/nginx/conf.d/
COPY resources/nginx/sites-enabled/app.conf /etc/nginx/sites-enabled/

CMD ["/usr/sbin/nginx"]

EXPOSE 80