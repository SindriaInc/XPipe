#!/bin/bash

DEBUG=false

warDir="$(dirname "$0")"

mem_param=''

if [ `free -m  | grep Mem | awk '{print $2}'` -gt 7000 ] && grep -q lm /proc/cpuinfo; then 
    mem_param='-Xmx6G'; 
else 
    echo "unable to allocate 6G mem: some functions may not work as expected" >&2;
fi

cmdb_java="$(which java)"

if [ $(update-java-alternatives -l | grep 17 | awk '{print $3}') ]; then
    cmdb_java=$(update-java-alternatives -l | grep 17 | awk '{print $3}')/bin/java
    if "$DEBUG"; then
        echo "found java: "$cmdb_java
    fi
else
    if "$DEBUG"; then
        echo "unable to find correct java, using default: "$cmdb_java
    fi
fi

exec $cmdb_java --add-opens java.base/java.nio=ALL-UNNAMED $mem_param -cp "$warDir" 'org.cmdbuild.webapp.cli.Main' 'CM_START_FROM_WEBAPP_DIR' "$warDir" "$@"

