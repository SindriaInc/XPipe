ARG BUILD_ARCH
ARG TAG_ARCH
ARG TAG_SUFFIX
FROM --platform=linux/${BUILD_ARCH} prom/prometheus:${TAG_SUFFIX}

ARG BUILD_ARCH
ARG TAG_ARCH
ARG TAG_SUFFIX
ARG TAG_VERSION
ARG HOST_USER_UID
ARG TIMEZONE

LABEL org.opencontainers.image.authors="Sindria Inc. <info@sindria.org>"

LABEL \
	name="prometheus" \
	image="sindriainc/prometheus" \
	tag="${TAG_VERSION}" \
	vendor="sindria"

ENV TZ=${TIMEZONE}
ENV SINDRIA_USER="sindria"
ENV SINDRIA_USER_HOME="/home/sindria"

ENV MONITOR_LABEL="xpipe-monitor"
ENV ALERTMANAGER_SCHEME="http"
ENV ALERTMANAGER_HOST="monitoring-alert.local"
ENV ALERTMANAGER_PORT="9093"
ENV SCRAPE_CONFIG="monitoring"
ENV SELF_TOGGLE="0"
ENV SELF_HOST="monitoring-self.local"
ENV SELF_PORT="9100"
ENV MONITORING_HOSTS=""
ENV APM_HOSTS=""

COPY resources/prometheus/prometheus.yml /etc/prometheus/prometheus.yml
COPY resources/prometheus/configs/apm.yml /etc/prometheus/apm.yml
COPY resources/prometheus/configs/monitoring.yml /etc/prometheus/monitoring.yml
COPY resources/prometheus/configs/self.yml /etc/prometheus/self.yml
COPY resources/prometheus/rules /etc/prometheus/rules

COPY resources/bin/entrypoint.sh /usr/local/bin/entrypoint.sh
ENTRYPOINT ["/bin/sh", "/usr/local/bin/entrypoint.sh"]

#CMD ["--config.file=/etc/prometheus/prometheus.yml", "--storage.tsdb.path=/prometheus", "--web.console.libraries=/usr/share/prometheus/console_libraries", "--web.console.templates=/usr/share/prometheus/consoles" ]