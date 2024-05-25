#!/usr/bin/env sh

# Setting global env
sed -i -E "s|@@DEFAULT_RECEIVER@@|${DEFAULT_RECEIVER}|g" /etc/alertmanager/config.yml
sed -i -E "s|@@DASHBOARD_URL@@|${DASHBOARD_URL}|g" /etc/alertmanager/sindria.tmpl

# Setting telegram env
sed -i -E "s|@@TELEGRAM_BOT_TOKEN@@|${TELEGRAM_BOT_TOKEN}|g" /etc/alertmanager/config.yml
sed -i -E "s|@@TELEGRAM_CHAT_ID@@|${TELEGRAM_CHAT_ID}|g" /etc/alertmanager/config.yml

# Setting teams env
sed -i -E "s|@@TEAMS_WEBHOOK_URL@@|${TEAMS_WEBHOOK_URL}|g" /etc/alertmanager/config.yml

/bin/alertmanager --config.file=/etc/alertmanager/config.yml --storage.path=/alertmanager
