ARG ARCH
FROM sindriainc/openjdk:3.0.0-21-${ARCH}

ARG ARCH
ARG TAG_VERSION
ARG TAG_SUFFIX
ARG HOST_USER_UID
ARG TIMEZONE

LABEL \
	name="bungeecord" \
	image="sindriainc/bungeecord" \
	tag="${TAG_VERSION}-${TAG_SUFFIX}" \
	vendor="sindria"

ENV TZ=${TIMEZONE}
ENV SINDRIA_USER="sindria"
ENV SINDRIA_USER_HOME="/home/sindria"
ENV SCREENDIR=$HOME/.screen
ENV BUNGEECORD_VERSION=${TAG_SUFFIX}
ENV JAVA_OPTS="-Xmx1024M -Xms1024M"
ENV JMX_EXPORTER_PORT="8080"

USER root

# Install bungeecord, setting timezone and reset permission
COPY resources/bin/bungeecord.sh /usr/local/bin/bungeecord.sh
COPY resources/bin/entrypoint.sh /usr/local/bin/entrypoint.sh
# https://sindria-minecraft-packages.s3.eu-south-1.amazonaws.com/BungeeCord.jar
RUN wget https://sindria-minecraft-packages.s3.eu-south-1.amazonaws.com/BungeeCord.jar -P /var/www/app && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && \
    chown ${SINDRIA_USER}:${SINDRIA_USER} /usr/local/bin/bungeecord.sh && \
    chmod +x /usr/local/bin/bungeecord.sh && \
    chown ${SINDRIA_USER}:${SINDRIA_USER} /usr/local/bin/entrypoint.sh && \
    chmod +x /usr/local/bin/entrypoint.sh && \
    mkdir -p /var/www/app/plugins && \
    mkdir -p /var/www/app/cache && \
    touch /var/www/app/cache/config.yml && \
    touch /var/www/app/cache/locations.yml && \
    touch /var/www/app/cache/proxy.log && \
    touch /var/www/app/cache/proxy.log.lck && \
    ln -s /var/www/app/cache/config.yml /var/www/app/config.yml && \
    ln -s /var/www/app/cache/locations.yml /var/www/app/locations.yml && \
    ln -s /var/www/app/cache/proxy.log /var/www/app/proxy.log && \
    ln -s /var/www/app/cache/proxy.log.lck /var/www/app/proxy.log.lck && \
    chmod -R 771 /var/www/app && \
    chown -R ${SINDRIA_USER}:${SINDRIA_USER} /var/www/app

#java -Xmx1024M -Xms1024M -jar BungeeCord.jar
ENTRYPOINT exec java -javaagent:/opt/jmx_prometheus_javaagent.jar=$JMX_EXPORTER_PORT:/opt/config.yaml $JAVA_OPTS -jar BungeeCord.jar

USER ${SINDRIA_USER}