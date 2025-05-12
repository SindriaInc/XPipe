#!/usr/bin/env sh

APEX="'"
COMMA=","

# Setting global env
sed -i -E "s|@@MONITOR_LABEL@@|${MONITOR_LABEL}|g" /etc/prometheus/prometheus.yml

# Setting alertmanager env
sed -i -E "s|@@ALERTMANAGER_SCHEME@@|${ALERTMANAGER_SCHEME}|g" /etc/prometheus/prometheus.yml
sed -i -E "s|@@ALERTMANAGER_HOST@@|${ALERTMANAGER_HOST}|g" /etc/prometheus/prometheus.yml
sed -i -E "s|@@ALERTMANAGER_PORT@@|${ALERTMANAGER_PORT}|g" /etc/prometheus/prometheus.yml

# Setting scrape config env
sed -i -E "s|@@SCRAPE_CONFIG@@|${SCRAPE_CONFIG}|g" /etc/prometheus/prometheus.yml

# Setting self toggle env
if [ "${SELF_TOGGLE}" == "1" ]; then
  sed -i -E "s|#@@||g" /etc/prometheus/prometheus.yml
fi

# Setting scrape config self env
sed -i -E "s|@@SELF_HOST@@|${SELF_HOST}|g" /etc/prometheus/self.yml
sed -i -E "s|@@SELF_PORT@@|${SELF_PORT}|g" /etc/prometheus/self.yml

# Parse MONITORING_HOSTS env separated by ;

TOTAL=""
FORMATTED_MONITORING_HOSTS=""

for i in $(echo $MONITORING_HOSTS | tr ";" "\n")
do
  ENTRY=$APEX$i$APEX
  SINGLE=$ENTRY$COMMA
  TOTAL=$TOTAL$SINGLE
done

FORMATTED_MONITORING_HOSTS=${TOTAL::len-1}

# Setting scrape config monitoring env
sed -i -E "s|@@MONITORING_HOSTS@@|${FORMATTED_MONITORING_HOSTS}|g" /etc/prometheus/monitoring.yml

# Parse APM_HOSTS env separated by ;

TOTAL=""
FORMATTED_APM_HOSTS=""

for i in $(echo $APM_HOSTS | tr ";" "\n")
do
  ENTRY=$APEX$i$APEX
  SINGLE=$ENTRY$COMMA
  TOTAL=$TOTAL$SINGLE
done

FORMATTED_APM_HOSTS=${TOTAL::len-1}

# Setting scrape config apm env
sed -i -E "s|@@APM_HOSTS@@|${FORMATTED_APM_HOSTS}|g" /etc/prometheus/apm.yml

/bin/prometheus --config.file=/etc/prometheus/prometheus.yml --storage.tsdb.path=/prometheus --web.console.libraries=/usr/share/prometheus/console_libraries --web.console.templates=/usr/share/prometheus/consoles