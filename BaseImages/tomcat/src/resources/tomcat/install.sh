#!/usr/bin/env bash

TOMCAT_VERSION=$1
JAVA_VERSION=$2

case ${TOMCAT_VERSION} in
    8.0.53)
        #wget https://dlcdn.apache.org/tomcat/tomcat-8/v8.0.53/bin/apache-tomcat-8.0.53.tar.gz -P /opt
        wget https://dlcdn.apache.org/tomcat/tomcat-8/v8.5.93/bin/apache-tomcat-8.5.93.tar.gz -P /opt
        tar xzf /opt/apache-tomcat-${TOMCAT_VERSION}.tar.gz -C /opt
        rm -rf /opt/*.tar.gz
        mv /opt/apache-tomcat-${TOMCAT_VERSION} /opt/tomcat
        ;;
    8.5.93)
        wget https://dlcdn.apache.org/tomcat/tomcat-8/v8.5.93/bin/apache-tomcat-8.5.93.tar.gz -P /opt
        tar xzf /opt/apache-tomcat-${TOMCAT_VERSION}.tar.gz -C /opt
        rm -rf /opt/*.tar.gz
        mv /opt/apache-tomcat-${TOMCAT_VERSION} /opt/tomcat
        ;;
    9.0.80)
        wget https://dlcdn.apache.org/tomcat/tomcat-9/v9.0.80/bin/apache-tomcat-9.0.80.tar.gz -P /opt
        tar xzf /opt/apache-tomcat-${TOMCAT_VERSION}.tar.gz -C /opt
        rm -rf /opt/*.tar.gz
        mv /opt/apache-tomcat-${TOMCAT_VERSION} /opt/tomcat
        ;;
    10.1.13)
        if [[ "${JAVA_VERSION}" == "8" ]]; then
            echo "Unsupported version, abort."
            exit 0
        fi

        wget https://dlcdn.apache.org/tomcat/tomcat-10/v10.1.13/bin/apache-tomcat-10.1.13.tar.gz -P /opt
        tar xzf /opt/apache-tomcat-${TOMCAT_VERSION}.tar.gz -C /opt
        rm -rf /opt/*.tar.gz
        mv /opt/apache-tomcat-${TOMCAT_VERSION} /opt/tomcat
        ;;
    *)
        echo "Invalid Tomcat version specified"
        exit 1
    ;;
esac
