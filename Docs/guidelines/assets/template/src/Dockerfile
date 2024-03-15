FROM sindriainc/<image-name>:<tag>

ARG TAG_VERSION
ARG HOST_USER_UID
ARG TIMEZONE

WORKDIR /var/www/app

LABEL \
        name="<repo-name>" \
        image="registry.sindria.org/<project-namespace>/<repo-name>" \
        tag="${TAG_VERSION}" \
        vendor="sindria"

ENV DEBIAN_FRONTEND="noninteractive"
ENV TZ=${TIMEZONE}
ENV SINDRIA_USER="sindria"
ENV SINDRIA_USER_HOME="/home/sindria"

# Install application
COPY ./ /var/www/app

# Setting Timezone and Fixing permission
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && \
    chmod -R 770 /var/www/app && \
    chown -R ${SINDRIA_USER}:${SINDRIA_USER} /var/www/app

CMD ["/bin/bash", "-c"]