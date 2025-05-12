FROM logstash:7.13.3

ARG TAG_VERSION

ENV ELASTICSEARCH_HOST="elasticsearch.local"
ENV ELASTICSEARCH_PROTOCOL="http"
ENV ELASTICSEARCH_PORT="9200"
ENV ELASTICSEARCH_CONTROL_CENTER_HOST="admin-elasticsearch.local"
ENV ELASTICSEARCH_CONTROL_CENTER_PROTOCOL="http"
ENV ELASTICSEARCH_CONTROL_CENTER_PORT="9200"
ENV ELASTICSEARCH_INDEX_SUFFIX="example"

LABEL org.opencontainers.image.authors="Sindria Inc. <info@sindria.org>"

LABEL \
	name="logstash" \
	image="sindriainc/logstash" \
	tag="${TAG_VERSION}" \
	vendor="sindria"

RUN rm -f /usr/share/logstash/pipeline/logstash.conf

COPY resources/config/logstash.yml /usr/share/logstash/config/logstash.yml
COPY resources/pipeline/logstash.conf /usr/share/logstash/pipeline/logstash.conf

USER root

COPY resources/bin/docker-entrypoint.sh /usr/local/bin/docker-entrypoint

RUN chmod 644 /usr/share/logstash/config/logstash.yml && \
    chown logstash:root /usr/share/logstash/config/logstash.yml && \
    chmod 644 /usr/share/logstash/pipeline/* && \
    chown -R logstash:root /usr/share/logstash/pipeline/ && \
    chmod +x /usr/local/bin/docker-entrypoint

USER logstash