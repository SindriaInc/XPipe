ARG ARCH
#FROM ${ARCH}prom/alertmanager:v0.26.0
FROM prom/alertmanager:v0.26.0

ARG TAG_VERSION
ARG HOST_USER_UID
ARG TIMEZONE

LABEL org.opencontainers.image.authors="Sindria Inc. <info@sindria.org>"

LABEL \
	name="alertmanager" \
	image="sindriainc/alertmanager" \
	tag="${TAG_VERSION}" \
	vendor="sindria"

ENV TZ=${TIMEZONE}
ENV SINDRIA_USER="sindria"
ENV SINDRIA_USER_HOME="/home/sindria"

ENV DEFAULT_RECEIVER="telegram"
ENV DASHBOARD_URL="https://monitoring.sindria.org"
ENV TELEGRAM_BOT_TOKEN=""
ENV TELEGRAM_CHAT_ID=""
ENV TEAMS_WEBHOOK_URL=""

# Setting config
COPY resources/alertmanager/config.yml /etc/alertmanager/config.yml
COPY resources/alertmanager/sindria.tmpl /etc/alertmanager/sindria.tmpl

# Setting entrypoint
COPY resources/bin/entrypoint.sh /usr/local/bin/entrypoint.sh
ENTRYPOINT ["/bin/sh", "/usr/local/bin/entrypoint.sh"]