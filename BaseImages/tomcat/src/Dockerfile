ARG ARCH
ARG JAVA_VERSION
FROM sindriainc/openjdk:2.1.0-${JAVA_VERSION}-${ARCH}

ARG JAVA_VERSION
ARG TAG_VERSION
ARG TAG_SUFFIX
ARG HOST_USER_UID
ARG TIMEZONE

LABEL org.opencontainers.image.authors="Sindria Inc. <info@sindria.org>"

LABEL \
	name="tomcat" \
	image="sindriainc/tomcat" \
	tag="${TAG_VERSION}-${TAG_SUFFIX}-${JAVA_VERSION}" \
	vendor="sindria"

ENV TZ=${TIMEZONE}
ENV SINDRIA_USER="sindria"
ENV SINDRIA_USER_HOME="/home/sindria"
ENV TOMCAT_VERSION=${TAG_SUFFIX}
ENV JAVA_VERSION=${JAVA_VERSION}
ENV CATALINA_HOME="/opt/tomcat"
ENV TOMCAT_PROXY_FQDN=""

# Install tomcat
COPY resources/tomcat/install.sh ${SINDRIA_USER_HOME}
RUN bash ${SINDRIA_USER_HOME}/install.sh ${TOMCAT_VERSION} ${JAVA_VERSION} && \
    rm -rf ${SINDRIA_USER_HOME}/install.sh && \
    rm -rf ${CATALINA_HOME}/webapps/ROOT && \
    rm -rf ${CATALINA_HOME}/webapps/docs && \
    rm -rf ${CATALINA_HOME}/webapps/examples
COPY resources/tomcat/tomcat-users.xml ${CATALINA_HOME}/conf/tomcat-users.xml
COPY resources/manager/context.xml ${CATALINA_HOME}/webapps/manager/META-INF/context.xml
COPY resources/tomcat/postgresql-42.6.0.jar ${CATALINA_HOME}/lib/postgresql-42.6.0.jar

# Setup tomcat for proxy - ingress
#COPY resources/tomcat/server.xml ${CATALINA_HOME}/conf/server.xml
#RUN sed -i -E "s|@@FQDN@@|${TOMCAT_PROXY_FQDN}|g" ${CATALINA_HOME}/conf/server.xml

# Setting timezone and permissions
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && \
    chown -R ${SINDRIA_USER}:${SINDRIA_USER} ${CATALINA_HOME}

WORKDIR /opt/tomcat

USER ${SINDRIA_USER}

ENTRYPOINT ["/opt/tomcat/bin/startup.sh"]

EXPOSE 8080