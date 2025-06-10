# Build Stage
FROM sindriainc/openjdk:2.0.0-17 as builder

# Add source code
COPY ./ /var/www/app

WORKDIR /var/www/app

# Build dependecies
RUN mvn compile

# Build Artifact
RUN mvn package

# Push Artifact
# TODO: push jar artifact

# Production Stage
FROM sindriainc/openjdk:2.1.0-17

WORKDIR /var/www/app

ARG TAG_VERSION
ARG HOST_USER_UID
ARG TIMEZONE

LABEL \
	name="xpipe-policies" \
	image="sindriaproject/xpipe-policies" \
	tag="${TAG_VERSION}" \
	vendor="sindria"

ENV DEBIAN_FRONTEND="noninteractive"
ENV TZ=${TIMEZONE}
ENV SINDRIA_USER="sindria"
ENV SINDRIA_USER_HOME="/home/sindria"
ENV JAVA_OPTS=""

# Install application
COPY --from=builder /var/www/app/target/xpipe-policies-${TAG_VERSION}.jar /var/www/app/app.jar

# Setting Timezone and Fixing permission
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && \
    chmod -R 770 /var/www/app && \
    chown -R ${SINDRIA_USER}:${SINDRIA_USER} /var/www/app

ENTRYPOINT exec java $JAVA_OPTS -jar app.jar

USER ${SINDRIA_USER}