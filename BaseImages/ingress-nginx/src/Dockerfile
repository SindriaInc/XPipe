ARG ARCH
FROM ${ARCH}/nginx:latest

ARG TAG_VERSION
ARG HOST_USER_UID
ARG TIMEZONE

LABEL \
	name="ingress-nginx" \
	image="sindriainc/ingress-nginx" \
	tag="${TAG_VERSION}" \
	vendor="sindria"

ENV DEBIAN_FRONTEND="noninteractive"
ENV TZ=${TIMEZONE} 

RUN usermod -u ${HOST_USER_UID} nginx && \
    groupmod -g ${HOST_USER_UID} nginx && \
    mkdir -p /etc/nginx/certs && \
    mkdir -p /etc/nginx/htpasswd

COPY resources/nginx/nginx.conf /etc/nginx/nginx.conf
COPY resources/nginx/snippets /etc/nginx/snippets
COPY resources/nginx/ssl /etc/nginx/ssl
