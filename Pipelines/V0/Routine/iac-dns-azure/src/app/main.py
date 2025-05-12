#!/usr/bin/env python3

import os
import subprocess
import threading
import concurrent.futures
import json

import helpers
import service

AZURE_SUBSCRIPTION_ID = os.environ["AZURE_SUBSCRIPTION_ID"]
AZURE_CLIENT_ID = os.environ["AZURE_CLIENT_ID"]
AZURE_SECRET = os.environ["AZURE_SECRET"]
AZURE_TENANT = os.environ["AZURE_TENANT"]
AZURE_RESOURCE_GROUP = os.environ["AZURE_RESOURCE_GROUP"]
AZURE_STORAGE_ACCOUNT = os.environ["AZURE_STORAGE_ACCOUNT"]
AZURE_STORAGE_ACCESS_KEY = os.environ["AZURE_STORAGE_ACCESS_KEY"]
AZURE_STORAGE_CONNECTION_STRING = os.environ["AZURE_STORAGE_CONNECTION_STRING"]

# # Pycharm pydevd
# PYCHARM_PYDEVD_ENABLED = int(os.getenv('PYCHARM_PYDEVD_ENABLED'))
# PYCHARM_PYDEVD_HOST = str(os.getenv('PYCHARM_PYDEVD_HOST'))
# PYCHARM_PYDEVD_PORT = int(os.getenv('PYCHARM_PYDEVD_PORT'))
#
# # Enable pydevd debugger
# if (PYCHARM_PYDEVD_ENABLED):
#     import pydevd_pycharm
#     pydevd_pycharm.settrace(PYCHARM_PYDEVD_HOST, port=PYCHARM_PYDEVD_PORT, stdoutToServer=True, stderrToServer=True)



# Create azure dns entry - return void
def create_azure_dns(entry):

    common = helpers.common()
    common_azure = common['defaults']['dns']['azure']

    # Filter common values
    if not 'rg' in entry:
        rg = common_azure['rg']
    else:
        rg = entry["rg"]

    if not 'domain' in entry:
        if not 'domain' in common_azure:
            domain = common['domain']
        else:
            domain = common_azure['domain']
    else:
        domain = entry["domain"]

    if not 'ttl' in entry:
        ttl = common_azure['ttl']
    else:
        ttl = entry["ttl"]


    # Login
    subprocess.call(["az", "login", "--service-principal", "-u", AZURE_CLIENT_ID, "-p", AZURE_SECRET, "--tenant", AZURE_TENANT])


    value = entry['value']

    if (service.check_vm_name(entry)):
        value = service.find_public_ip_address_by_name(entry['vm_name'])

    # Create new dns record
    stdout = subprocess.check_output(['az', 'network', 'dns', 'record-set', entry['type'], 'add-record', '-g', rg, '-z', domain, '-n', entry['name'], '-a', value, '--ttl', ttl], universal_newlines=True)
    print(stdout)




# Multi process azure dns records
def process_azure(azure_dns_records):
    with concurrent.futures.ProcessPoolExecutor() as executor:
        results = [executor.submit(create_azure_dns, entry) for entry in azure_dns_records]

        for f in concurrent.futures.as_completed(results):
            f.result()


# Main
def main():
    data = helpers.app()

    azure_dns_records = []

    for k,entry in data['azure'].items():

        # Filter azure deployments
        azure_dns_records.append(entry)


    t1 = threading.Thread(target=process_azure, args=[azure_dns_records])

    t1.start()

    t1.join()



# Execute
if __name__ == '__main__':
    main()