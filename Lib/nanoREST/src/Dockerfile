FROM sindriainc/openjdk:1.1.0-14

WORKDIR /var/www/app

ARG TAG_VERSION
ARG HOST_USER_UID
ARG TIMEZONE

LABEL \
	name="nanoREST" \
	image="sindriainc/nanoREST" \
	tag="${TAG_VERSION}" \
	vendor="sindria"

ENV DEBIAN_FRONTEND="noninteractive"
ENV TZ=${TIMEZONE}
ENV SINDRIA_USER="sindria"
ENV SINDRIA_USER_HOME="/home/sindria"

# Install application
COPY ./target/nanoREST-0.1.0.jar /var/www/app

# Setting Timezone and Fixing permission
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && \
    chmod -R 770 /var/www/app && \
    chown -R ${SINDRIA_USER}:${SINDRIA_USER} /var/www/app

CMD ["java", "-jar", "nanoREST-0.1.0.jar"]
