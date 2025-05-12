FROM docker.elastic.co/beats/filebeat:7.13.3

ARG TAG_VERSION

ENV LOGSTASH_HOST "logstash.local"

LABEL org.opencontainers.image.authors="Sindria Inc. <info@sindria.org>"

LABEL \
	name="filebeat" \
	image="sindriainc/filebeat" \
	tag="${TAG_VERSION}" \
	vendor="sindria"

COPY resources/filebeat.yml /usr/share/filebeat/filebeat.yml

USER root

COPY resources/bin/docker-entrypoint.sh /usr/local/bin/docker-entrypoint

RUN yum install -y sudo && \
    usermod -a -G wheel filebeat && \
    chmod 644 /etc/sudoers

COPY resources/etc/sudoers /etc/sudoers

RUN chmod 644 /etc/sudoers && \
    chmod -R 755 /etc/sudoers.d

RUN usermod filebeat -u 997 && \
    groupmod -g 996 filebeat && \
    mkdir -p /var/ossec && \
    mkdir -p /var/ossec/data && \
    chmod 644 /usr/share/filebeat/filebeat.yml && \
    chown root:filebeat /usr/share/filebeat/filebeat.yml && \
    chmod +x /usr/local/bin/docker-entrypoint

USER filebeat
