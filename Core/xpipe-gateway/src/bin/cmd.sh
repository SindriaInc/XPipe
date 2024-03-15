#!/usr/bin/env bash

bash -c "sudo chmod +x /usr/local/bin/startup.sh; /usr/local/bin/startup.sh& /var/www/app/bin/run-scheduler.sh"