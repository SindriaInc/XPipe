## Build Stage
#FROM sindriainc/openjdk:1.5.0-17 as builder
#
## Add source code
#COPY ./ /var/www/app
#
## Build dependecies
#RUN mvn compile
#
## Build Artifact
#RUN mvn package
#
## Push Artifact
## TODO: push phar artifact

# Production Stage
FROM sindriainc/openjdk:1.5.0-17

WORKDIR /var/www/app

ARG TAG_VERSION
ARG HOST_USER_UID
ARG TIMEZONE

LABEL \
	name="xp-xdev" \
	image="sindriaproject/xp-xdev" \
	tag="${TAG_VERSION}" \
	vendor="sindria"

ENV DEBIAN_FRONTEND="noninteractive"
ENV TZ=${TIMEZONE}
ENV SINDRIA_USER="sindria"
ENV SINDRIA_USER_HOME="/home/sindria"

# Install application
#COPY --from=builder /var/www/app/target/xp-xdev-0.1.0.jar /var/www/app
COPY target/xp-xdev-0.1.0.jar /var/www/app/app.jar

# Setting Timezone and Fixing permission
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && \
    chmod -R 770 /var/www/app && \
    chown -R ${SINDRIA_USER}:${SINDRIA_USER} /var/www/app

CMD ["java", "-jar", "app.jar"]