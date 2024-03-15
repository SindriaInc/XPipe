#!/usr/bin/env bash

sudo ln -s /var/www/app/supervisor/queue.conf /etc/supervisor/conf.d/queue.conf

sudo chmod +x /usr/local/bin/startup.sh
/usr/local/bin/startup.sh&

chmod +x /var/www/app/bin/run-scheduler.sh
/var/www/app/bin/run-scheduler.sh
