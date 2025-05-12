#!/usr/bin/env bash

TOMCAT_VERSION=$1
JAVA_VERSION=$2

case ${TOMCAT_VERSION} in
    9.0.99)
        wget https://dlcdn.apache.org/tomcat/tomcat-9/v9.0.99/bin/apache-tomcat-9.0.99.tar.gz -P /opt
        tar xzf /opt/apache-tomcat-${TOMCAT_VERSION}.tar.gz -C /opt
        rm -rf /opt/*.tar.gz
        mv /opt/apache-tomcat-${TOMCAT_VERSION} /opt/tomcat
        ;;
    9.0.100)
        wget https://dlcdn.apache.org/tomcat/tomcat-9/v9.0.100/bin/apache-tomcat-9.0.100.tar.gz -P /opt
        tar xzf /opt/apache-tomcat-${TOMCAT_VERSION}.tar.gz -C /opt
        rm -rf /opt/*.tar.gz
        mv /opt/apache-tomcat-${TOMCAT_VERSION} /opt/tomcat
        ;;
    10.1.36)
        if [[ "${JAVA_VERSION}" == "8" ]]; then
            echo "Unsupported version, abort."
            exit 0
        fi

        wget https://dlcdn.apache.org/tomcat/tomcat-10/v10.1.36/bin/apache-tomcat-10.1.36.tar.gz -P /opt
        tar xzf /opt/apache-tomcat-${TOMCAT_VERSION}.tar.gz -C /opt
        rm -rf /opt/*.tar.gz
        mv /opt/apache-tomcat-${TOMCAT_VERSION} /opt/tomcat
        ;;
    11.0.4)
        if [[ "${JAVA_VERSION}" == "8" ]]; then
            echo "Unsupported version, abort."
            exit 0
        fi

        wget https://dlcdn.apache.org/tomcat/tomcat-11/v11.0.4/bin/apache-tomcat-11.0.4.tar.gz -P /opt
        tar xzf /opt/apache-tomcat-${TOMCAT_VERSION}.tar.gz -C /opt
        rm -rf /opt/*.tar.gz
        mv /opt/apache-tomcat-${TOMCAT_VERSION} /opt/tomcat
        ;;
    *)
        echo "Invalid Tomcat version specified"
        exit 1
    ;;
esac
