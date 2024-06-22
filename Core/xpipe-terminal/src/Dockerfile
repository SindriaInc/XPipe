ARG ARCH
FROM sindriainc/python:3.0.0-3.10-${ARCH}

WORKDIR /var/www/app

ARG TAG_VERSION
ARG HOST_USER_UID
ARG TIMEZONE

LABEL \
	name="xpipe-terminal" \
	image="sindriaproject/xpipe-terminal" \
	tag="${TAG_VERSION}" \
	vendor="sindria"

ENV TZ=${TIMEZONE}
ENV SINDRIA_USER="sindria"
ENV SINDRIA_USER_HOME="/home/sindria"

USER root

# Install application
COPY ./ /var/www/app
RUN pip install -r requirements.txt && \
    rm -f /var/www/app/requirements.txt && \
    ln -s /var/www/app/app/main.py /usr/local/bin/app

# Override webssh template
RUN cp /var/www/app/templates/index.html /usr/local/lib/python3.10/site-packages/webssh/templates/index.html

# Setting Timezone and Fixing permission
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && \
    chmod -R 771 /var/www/app && \
    chown -R ${SINDRIA_USER}:${SINDRIA_USER} /var/www/app

ENTRYPOINT ["wssh", "--xsrf=False", "--origin='https://terminal-xpipe.sindria.org,https://demo-xpipe.sindria.org,https://local-terminal-xpipe.sindria.org,https://local-demo-xpipe.sindria.org,http://172.16.10.1,terminal-xpipe.sindria.org,demo-xpipe.sindria.org,local-terminal-xpipe.sindria.org,local-demo-xpipe.sindria.org,localhost,172.16.10.1'", "--timeout=1440", "--delay=1440", "--debug", "&"]

USER ${SINDRIA_USER}