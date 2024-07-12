FROM sindriainc/ingress-nginx:2.0.0

ARG TAG_VERSION
ARG HOST_USER_UID
ARG TIMEZONE

ENV TAG_VERSION ${TAG_VERSION}
ENV APP_DOMAIN=""
ENV APP_UPSTREAM=""
ENV APP_VHOST=""

LABEL \
	name="ekore-ingress" \
	image="ekoresrl/ekore-ingress" \
	tag="${TAG_VERSION}" \
	vendor="sindria"

#COPY resources/nginx/nginx.conf /etc/nginx/nginx.conf

COPY resources/scripts/hosts.sh /etc/nginx/scripts/
COPY resources/scripts/sites.sh /etc/nginx/scripts/

COPY resources/hosts.production /etc
COPY resources/hosts.staging /etc
COPY resources/hosts.development /etc
COPY resources/hosts.local /etc

COPY resources/nginx/sites-available/*.conf /etc/nginx/tmp/

RUN openssl dhparam -out /etc/nginx/ssl/dhparam.pem 2048 && \
    bash /etc/nginx/scripts/hosts.sh && \
    bash /etc/nginx/scripts/sites.sh && \ 
    rm -rf /etc/nginx/scripts

COPY resources/nginx/htpasswd /etc/nginx/htpasswd
COPY resources/nginx/certs /etc/nginx/certs

COPY cmd.sh /cmd.sh
CMD ["/bin/bash", "/cmd.sh"]

EXPOSE 80 443
