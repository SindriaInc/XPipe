ARG ARCH
ARG TAG_SUFFIX
#FROM --platform=linux/${ARCH} qemux/qemu-docker:${TAG_SUFFIX}
FROM qemux/qemu-docker:${TAG_SUFFIX}

ARG TAG_SUFFIX
ARG TAG_VERSION
ARG HOST_USER_UID
ARG TIMEZONE

LABEL org.opencontainers.image.authors="Sindria Inc. <info@sindria.org>"

LABEL \
	name="qemu" \
	image="sindriainc/qemu" \
	tag="${TAG_VERSION}-${TAG_SUFFIX}" \
	vendor="sindria"

USER root

ENV TZ=${TIMEZONE}
ENV SINDRIA_USER="sindria"
ENV SINDRIA_USER_HOME="/home/sindria"

ENV NETWORK_TYPE="bridge"
ENV NETWORK_IFACE="net1"

# Update and install packages
RUN apt-get update && apt-get install -y iproute2 curl git wget vim tini && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

SHELL ["/bin/bash", "-c"]

# Setting entrypoint
COPY resources/bin/entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

ENTRYPOINT ["/usr/bin/tini", "-s", "--", "/entrypoint.sh"]