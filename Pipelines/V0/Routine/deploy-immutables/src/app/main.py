#!/usr/bin/env python3

import os
import subprocess
import threading
import concurrent.futures

import helpers

# # Pycharm pydevd
# PYCHARM_PYDEVD_ENABLED = int(os.getenv('PYCHARM_PYDEVD_ENABLED'))
# PYCHARM_PYDEVD_HOST = str(os.getenv('PYCHARM_PYDEVD_HOST'))
# PYCHARM_PYDEVD_PORT = int(os.getenv('PYCHARM_PYDEVD_PORT'))
#
# # Enable pydevd debugger
# if (PYCHARM_PYDEVD_ENABLED):
#     import pydevd_pycharm
#     pydevd_pycharm.settrace(PYCHARM_PYDEVD_HOST, port=PYCHARM_PYDEVD_PORT, stdoutToServer=True, stderrToServer=True)


# Sample lightsail
#aws lightsail create-instances-from-snapshot --instance-snapshot-name WordPress-1-1569866208 --instance-names WordPress-2 --availability-zone us-west-2a --bundle-id medium_2_0

# Sample ec2
#aws ec2 run-instances --image-id ami-xxxxxxxx --count 1 --instance-type t2.micro --key-name MyKeyPair --security-group-ids sg-903004f8 --subnet-id subnet-6e7f829e


# Create lightsail immutable
def create_lightsail(immutable):
    subprocess.call(['aws', 'lightsail','create-instances-from-snapshot', '--instance-snapshot-name', immutable['refer'], '--instance-names', immutable['name'], '--availability-zone', immutable['zone'], '--bundle-id', immutable['bundle']])

# Create ec2 immutable
def create_ec2(immutable):
    subprocess.call(['aws', 'ec2', 'run-instances', '--image-id', immutable['refer'], '--instance-names', immutable['name'], '--availability-zone', immutable['zone'], '--bundle-id', immutable['bundle']])

# Multi process lightsail immutable
def process_lightsail(lightsail_immutables):
    with concurrent.futures.ProcessPoolExecutor() as executor:
        results = [executor.submit(create_lightsail, immutable) for immutable in lightsail_immutables]

        for f in concurrent.futures.as_completed(results):
            f.result()

# Multi process ec2 immutable
def process_ec2(ec2_immutables):
    with concurrent.futures.ProcessPoolExecutor() as executor:
        results = [executor.submit(create_lightsail, immutable) for immutable in ec2_immutables]

        for f in concurrent.futures.as_completed(results):
            f.result()


# Main
def main():
    data = helpers.app()

    lightsail_immutables = []
    ec2_immutables = []

    for k,immutable in data['immutables'].items():

        # Filter lightsail deployments
        if (immutable['type'] == "lightsail"):
            lightsail_immutables.append(immutable)

        # Filter ec2 deployments
        if (immutable['type'] == "ec2"):
            ec2_immutables.append(immutable)


    t1 = threading.Thread(target=process_lightsail, args=[lightsail_immutables])
    t2 = threading.Thread(target=process_ec2, args=[ec2_immutables])

    t1.start()
    t2.start()

    t1.join()
    t2.join()


# Execute
if __name__ == '__main__':
    main()