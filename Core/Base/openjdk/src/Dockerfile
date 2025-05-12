ARG ARCH
ARG TAG_SUFFIX
FROM ${ARCH}/openjdk:${TAG_SUFFIX}-slim

ARG TAG_SUFFIX
ARG TAG_VERSION
ARG HOST_USER_UID
ARG TIMEZONE

LABEL org.opencontainers.image.authors="Sindria Inc. <info@sindria.org>"

LABEL \
	name="openjdk" \
	image="sindriainc/openjdk" \
	tag="${TAG_VERSION}-${TAG_SUFFIX}" \
	vendor="sindria"

ENV DEBIAN_FRONTEND="noninteractive"
ENV TZ=${TIMEZONE}
ENV SINDRIA_USER="sindria"
ENV SINDRIA_USER_HOME="/home/sindria"
ENV JAVA_OPTS=""
ENV \
    # Set (restore) the default value of '-XX:MaxMetaspaceSize' to '100m',
    # despite the Base Image v1.10-1+ set it no more.
    GC_MAX_METASPACE_SIZE=100

ENV JMX_EXPORTER_VERSION="1.0.1"
ENV JMX_EXPORTER_PORT="8081"

# Update and install packages
RUN apt update && \
    apt install -y \
    bash \
    curl \
    screen \
    vim \
    wget \
    git \
    rsync \
    ca-certificates \
    tzdata && \
    apt clean all && \
    apt autoremove

# Install Maven
RUN apt install -y maven && \
    apt clean all && \
    apt autoremove

# Add custom maven settings
COPY resources/maven/settings.xml /usr/share/maven/conf

# Setting app user, timezone and permissions
RUN useradd ${SINDRIA_USER} -u ${HOST_USER_UID} -m -d ${SINDRIA_USER_HOME} -s /bin/bash && \
    groupmod ${SINDRIA_USER} -g ${HOST_USER_UID} && \
    mkdir -p /var/www && \
    mkdir -p /var/www/app && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && \
    chmod -R 775 /var/www/app && \
    chown -R ${SINDRIA_USER}:root /var/www/app && \
    mkdir -p /context && \
    chmod -R 755 /context && \
    chown -R ${SINDRIA_USER}:${SINDRIA_USER} /context

WORKDIR /var/www/app

# Install prometheus jmx_exporter
COPY resources/jmx_exporter/config.yaml /opt/config.yaml
RUN wget https://repo.maven.apache.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/${JMX_EXPORTER_VERSION}/jmx_prometheus_javaagent-${JMX_EXPORTER_VERSION}.jar -P /opt && \
    mv /opt/jmx_prometheus_javaagent-${JMX_EXPORTER_VERSION}.jar /opt/jmx_prometheus_javaagent.jar && \
    chown -R ${SINDRIA_USER}:root /opt && \
    chmod -R 775 /opt

#java -javaagent:/path/to/jmx_exporter.jar=<port>:<path/to/config.yaml> -jar your-java-application.jar
# - Replace `<port>` with a designated port number for exposing metrics and `<path/to/config.yaml>` with your configuration file's path.

SHELL ["/bin/bash", "-c"]

ENTRYPOINT exec java -javaagent:/opt/jmx_prometheus_javaagent.jar=$JMX_EXPORTER_PORT:/opt/config.yaml $JAVA_OPTS -jar app.jar

USER ${SINDRIA_USER}