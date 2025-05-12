#!/bin/bash -e

# Setting pipeline host local
sed -i -E "s|@@ELASTICSEARCH_HOST@@|${ELASTICSEARCH_HOST}|g" /usr/share/logstash/pipeline/logstash.conf
sed -i -E "s|@@ELASTICSEARCH_PORT@@|${ELASTICSEARCH_PORT}|g" /usr/share/logstash/pipeline/logstash.conf

# Setting pipeline host control center
sed -i -E "s|@@ELASTICSEARCH_CONTROL_CENTER_HOST@@|${ELASTICSEARCH_CONTROL_CENTER_HOST}|g" /usr/share/logstash/pipeline/logstash.conf
sed -i -E "s|@@ELASTICSEARCH_CONTROL_CENTER_PORT@@|${ELASTICSEARCH_CONTROL_CENTER_PORT}|g" /usr/share/logstash/pipeline/logstash.conf

# Setting pipeline index suffix
sed -i -E "s|@@ELASTICSEARCH_INDEX_SUFFIX@@|${ELASTICSEARCH_INDEX_SUFFIX}|g" /usr/share/logstash/pipeline/logstash.conf

# Setting monitoring host local
sed -i -E "s|@@ELASTICSEARCH_PROTOCOL@@|${ELASTICSEARCH_PROTOCOL}|g" /usr/share/logstash/config/logstash.yml
sed -i -E "s|@@ELASTICSEARCH_HOST@@|${ELASTICSEARCH_HOST}|g" /usr/share/logstash/config/logstash.yml
sed -i -E "s|@@ELASTICSEARCH_PORT@@|${ELASTICSEARCH_PORT}|g" /usr/share/logstash/config/logstash.yml

# Setting monitoring host control center
sed -i -E "s|@@ELASTICSEARCH_CONTROL_CENTER_PROTOCOL@@|${ELASTICSEARCH_CONTROL_CENTER_PROTOCOL}|g" /usr/share/logstash/config/logstash.yml
sed -i -E "s|@@ELASTICSEARCH_CONTROL_CENTER_HOST@@|${ELASTICSEARCH_CONTROL_CENTER_HOST}|g" /usr/share/logstash/config/logstash.yml
sed -i -E "s|@@ELASTICSEARCH_CONTROL_CENTER_PORT@@|${ELASTICSEARCH_CONTROL_CENTER_PORT}|g" /usr/share/logstash/config/logstash.yml


# Map environment variables to entries in logstash.yml.
# Note that this will mutate logstash.yml in place if any such settings are found.
# This may be undesirable, especially if logstash.yml is bind-mounted from the
# host system.
env2yaml /usr/share/logstash/config/logstash.yml

export LS_JAVA_OPTS="-Dls.cgroup.cpuacct.path.override=/ -Dls.cgroup.cpu.path.override=/ $LS_JAVA_OPTS"

if [[ -z $1 ]] || [[ ${1:0:1} == '-' ]] ; then
  exec logstash "$@"
else
  exec "$@"
fi