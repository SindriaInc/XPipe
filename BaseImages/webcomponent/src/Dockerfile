# Extends nginx-php base image
ARG ARCH
ARG TAG_SUFFIX
FROM sindriainc/nginx-php:7.0.0-local-wordpress-8.3

ARG TAG_VERSION
ARG TAG_SUFFIX
ARG HOST_USER_UID
ARG TIMEZONE

LABEL \
	name="sindriainc/webcomponent" \
	image="webcomponent" \
	tag="${TAG_VERSION}-${TAG_SUFFIX}" \
	vendor="sindria"

ENV HOST_USER_UID ${HOST_USER_UID}
ENV TZ=${TIMEZONE}
ENV BOOTSTRAP_USER="bootstrap"
ENV SINDRIA_USER="sindria"
ENV SINDRIA_USER_HOME="/home/sindria"
ENV DB_NAME=app
ENV DB_USER=user
ENV DB_PASSWORD=secret
ENV DB_HOST=127.0.0.1
ENV WP_WEBBLOG_TITLE="test"
ENV WP_USER_NAME="admin"
ENV WP_ADMIN_PW="secret"
ENV WP_PW_WEAK="on"
ENV WP_ADMIN_EMAIL="test@sindria.org"



WORKDIR /var/www/app

USER root

RUN wget https://wordpress.org/latest.zip && \
    unzip latest.zip && \
    mv /var/www/app/wordpress/* /var/www/app/ && \
    rm -r /var/www/app/wordpress && \
    rm /var/www/app/latest.zip

COPY resources/wp-config.php /var/www/app

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && \
    chmod -R 770 /var/www/app && \
    chown -R ${SINDRIA_USER}:${SINDRIA_USER} /var/www/app


COPY resources/bin/cmd.sh /usr/local/bin/cmd.sh

RUN chmod +x /usr/local/bin/cmd.sh
#ENTRYPOINT [""]
USER ${SINDRIA_USER}
CMD ["sudo", "--preserve-env", "su", "bootstrap", "-c", "/usr/local/bin/cmd.sh"]

