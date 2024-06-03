ARG ARCH
FROM sindriainc/openjdk:3.0.0-21-${ARCH}

ARG ARCH
ARG TAG_VERSION
ARG TAG_SUFFIX
ARG HOST_USER_UID
ARG TIMEZONE

LABEL \
	name="minecraft" \
	image="sindriainc/minecraft" \
	tag="${TAG_VERSION}-${TAG_SUFFIX}" \
	vendor="sindria"

ENV TZ=${TIMEZONE}
ENV SINDRIA_USER="sindria"
ENV SINDRIA_USER_HOME="/home/sindria"
ENV SCREENDIR=$HOME/.screen
ENV MINECRAFT_VERSION=${TAG_SUFFIX}
ENV JAVA_OPTS="-Xmx1024M -Xms1024M"
ENV JMX_EXPORTER_PORT="8080"

USER root

# Install minecraft, setting timezone and reset permission
COPY resources/minecraft/eula.txt /var/www/app/eula.txt
#COPY resources/minecraft/server.properties /var/www/app/server.properties
COPY resources/bin/minecraft.sh /usr/local/bin/minecraft.sh
COPY resources/bin/entrypoint.sh /usr/local/bin/entrypoint.sh
RUN wget https://sindria-minecraft-packages.s3.eu-south-1.amazonaws.com/server-${MINECRAFT_VERSION}.jar -P /var/www/app && \
    mv /var/www/app/server-${MINECRAFT_VERSION}.jar /var/www/app/server.jar && \
    mkdir -p /var/www/app/plugins && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && \
    chmod -R 771 /var/www/app && \
    chown -R ${SINDRIA_USER}:${SINDRIA_USER} /var/www/app && \
    chown ${SINDRIA_USER}:${SINDRIA_USER} /usr/local/bin/minecraft.sh && \
    chmod +x /usr/local/bin/minecraft.sh && \
    chown ${SINDRIA_USER}:${SINDRIA_USER} /usr/local/bin/entrypoint.sh && \
    chmod +x /usr/local/bin/entrypoint.sh


#java -Xmx1024M -Xms1024M -jar minecraft_server.1.17.1.jar nogui
ENTRYPOINT exec java -javaagent:/opt/jmx_prometheus_javaagent.jar=$JMX_EXPORTER_PORT:/opt/config.yaml $JAVA_OPTS -jar server.jar nogui

USER ${SINDRIA_USER}