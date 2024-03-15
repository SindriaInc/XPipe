FROM kotuko/<image-name>:<tag>

ARG TAG_VERSION
ARG HOST_USER_UID
ARG TIMEZONE

WORKDIR /var/www/app

LABEL \
        name="<repo-slug>" \
        image="kotuko/<repo-slug>" \
        tag="${TAG_VERSION}" \
        vendor="kotuko"

ENV DEBIAN_FRONTEND="noninteractive"
ENV TZ=${TIMEZONE}
ENV KOTUKO_USER="kotuko"
ENV KOTUKO_USER_HOME="/home/kotuko"

# Install application
COPY ./ /var/www/app

# Setting Timezone and Fixing permission
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && \
    chmod -R 770 /var/www/app && \
    chown -R ${KOTUKO_USER}:${KOTUKO_USER} /var/www/app

CMD ["/bin/bash", "-c"]