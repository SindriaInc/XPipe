#!/usr/bin/env python3

import os
import subprocess
import helpers

# Pycharm pydevd
PYCHARM_PYDEVD_ENABLED = int(os.getenv('PYCHARM_PYDEVD_ENABLED'))
PYCHARM_PYDEVD_HOST = str(os.getenv('PYCHARM_PYDEVD_HOST'))
PYCHARM_PYDEVD_PORT = int(os.getenv('PYCHARM_PYDEVD_PORT'))

# Enable pydevd debugger
if (PYCHARM_PYDEVD_ENABLED):
    import pydevd_pycharm
    pydevd_pycharm.settrace(PYCHARM_PYDEVD_HOST, port=PYCHARM_PYDEVD_PORT, stdoutToServer=True, stderrToServer=True)


# Sample lightsail
# aws lightsail create-instance-snapshot --instance-name WordPress-1 --instance-snapshot-name WordPress-Snapshot-1

# Main
def main():
    data = helpers.app()

    for k,blueprint in data['blueprints'].items():
        if (blueprint['type'] == "lightsail"):
            subprocess.call(['aws', 'lightsail','create-instance-snapshot', '--instance-name', blueprint['refer'], '--instance-snapshot-name', blueprint['name']])

# Execute
if __name__ == '__main__':
    main()