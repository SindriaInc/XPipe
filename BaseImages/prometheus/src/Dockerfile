ARG ARCH
ARG TAG_SUFFIX
FROM --platform=linux/${ARCH} prom/prometheus:${TAG_SUFFIX}
#FROM prom/prometheus:${TAG_SUFFIX}

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

# Setting entrypoint
#COPY resources/bin/entrypoint.sh /usr/local/bin/entrypoint.sh
#ENTRYPOINT ["/bin/sh", "/usr/local/bin/entrypoint.sh"]

CMD ["--config.file=/etc/prometheus/prometheus.yml", "--storage.tsdb.path=/prometheus", "--web.console.libraries=/usr/share/prometheus/console_libraries", "--web.console.templates=/usr/share/prometheus/consoles" ]