ARG ARCH
ARG TOMCAT_VERSION
ARG JAVA_VERSION
FROM sindriainc/tomcat:1.1.0-${TOMCAT_VERSION}-${JAVA_VERSION}-${ARCH}

ARG TAG_VERSION
ARG TAG_SUFFIX
ARG HOST_USER_UID
ARG TIMEZONE

LABEL org.opencontainers.image.authors="Sindria Inc. <info@sindria.org>"

LABEL \
	name="cmdbuild" \
	image="sindriainc/cmdbuild" \
	tag="${TAG_VERSION}-${TAG_SUFFIX}" \
	vendor="sindria"

WORKDIR /opt/tomcat

USER root

ENV TZ=${TIMEZONE}
ENV SINDRIA_USER="sindria"
ENV SINDRIA_USER_HOME="/home/sindria"
ENV CATALINA_HOME="/opt/tomcat"

ENV CMDBUILD_DB_USER postgres
ENV CMDBUILD_DB_PASSWORD postgres
ENV CMDBUILD_DB_PORT 5432
ENV CMDBUILD_DB_HOST cmdbuild-db.local
ENV CMDBUILD_DB_NAME app
ENV CMDBUILD_DB_DUMP empty

# Install dependencies
RUN apt update -y && \
    apt install -y \
    postgresql-client \
    zip \
    unzip && \
    apt clean all && \
    apt autoremove

# Install cmdbuild
RUN wget https://kumisystems.dl.sourceforge.net/project/cmdbuild/${TAG_SUFFIX}/cmdbuild-${TAG_SUFFIX}.war -P ${CATALINA_HOME}/webapps && \
    mv ${CATALINA_HOME}/webapps/cmdbuild-${TAG_SUFFIX}.war ${CATALINA_HOME}/webapps/ROOT.war && \
    cd ${CATALINA_HOME}/webapps && \
    unzip ROOT.war -d ROOT && \
    cd ${CATALINA_HOME} && \
    rm -Rf ${CATALINA_HOME}/webapps/ROOT.war
COPY resources/cmdbuild/database.conf ${CATALINA_HOME}/conf/ROOT/database.conf

# Setting timezone and permissions
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && \
    chown -R ${SINDRIA_USER}:${SINDRIA_USER} ${CATALINA_HOME}

USER ${SINDRIA_USER}

COPY resources/bin/entrypoint.sh /usr/local/bin/entrypoint.sh
ENTRYPOINT ["/bin/bash", "/usr/local/bin/entrypoint.sh"]

CMD ["catalina.sh", "run"]