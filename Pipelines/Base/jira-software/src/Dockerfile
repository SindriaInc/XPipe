ARG ARCH
ARG TAG_SUFFIX
FROM --platform=linux/${ARCH} atlassian/jira-software:${TAG_SUFFIX}-ubuntu-jdk17

ARG TAG_VERSION
ARG TAG_SUFFIX
ARG HOST_USER_UID
ARG TIMEZONE

LABEL org.opencontainers.image.authors="Sindria Inc. <info@sindria.org>"

LABEL \
	name="jira-software" \
	image="sindriainc/jira-software" \
	tag="${TAG_VERSION}-${TAG_SUFFIX}" \
	vendor="sindria"

ENV TZ=${TIMEZONE}
ENV SINDRIA_USER="sindria"
ENV SINDRIA_USER_HOME="/home/sindria"

# Install third party libs
# /opt/atlassian/jira/lib
# https://sindria-devops-share.s3.eu-south-1.amazonaws.com/mysql-connector-j-9.0.0.jar
RUN wget https://sindria-devops-share.s3.eu-south-1.amazonaws.com/mysql-connector-j-9.0.0.jar -P /opt/atlassian/jira/lib && \
    chmod 550 /opt/atlassian/jira/lib/mysql-connector-j-9.0.0.jar && \
    chown jira:root /opt/atlassian/jira/lib/mysql-connector-j-9.0.0.jar

# Setting timezone
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone