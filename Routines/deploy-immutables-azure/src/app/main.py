#!/usr/bin/env python3

import os
import subprocess
import sys
import threading
import concurrent.futures
import uuid
from hashlib import sha256
import json
from azure.core import *
from azure.identity import AzureCliCredential
from azure.mgmt.resource import ResourceManagementClient
from azure.mgmt.network import NetworkManagementClient
from azure.mgmt.compute import ComputeManagementClient

import helpers

DEPLOY_TEMPLATE = '/var/www/app/templates/deploy.template.ps1'
BUILD_DIRECTORY = '/var/www/app/.build'

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


# Build unique workspace - return string
def build_workspace(name):
    stringId = uuid.uuid4()
    salted_string = str(stringId) + str(name)
    hash_result = sha256(str(salted_string).encode())
    return hash_result.hexdigest()


# Find Security Group by name - return dict
def find_security_group(name):
    stdout = subprocess.check_output(['az', 'network', 'nsg', 'show', '-g', AZURE_RESOURCE_GROUP, '-n', name], universal_newlines=True)
    return json.loads(stdout)

# Check if immutable name already exists - return boolean
def check_immutable_by_name(name):
    query = "[?name=='{0}']".format(name)
    stdout = subprocess.check_output(['az', 'vm', 'list', '-d', '-o', 'json', '--query', query], universal_newlines=True)
    data = json.loads(stdout)

    if data:
        return True
    return False


# Create azure immutable by SDK - return void
def create_azure(immutable):

    common = helpers.common()
    common_azure = common['defaults']['immutables']['azure']

    # Filter common values
    if not 'rg' in immutable:
        rg = common_azure['rg']
    else:
        rg = immutable["rg"]

    if not 'region' in immutable:
        region = common_azure['region']
    else:
        region = immutable["region"]

    if not 'vpc' in immutable:
        vpc = common_azure['vpc']
    else:
        vpc = immutable["vpc"]

    if not 'subnet' in immutable:
        subnet = common_azure['subnet']
    else:
        subnet = immutable["subnet"]

    if not 'blueprint' in immutable:
        blueprint = common_azure['blueprint']
    else:
        blueprint = immutable["blueprint"]

    if not 'bundle' in immutable:
        bundle = common_azure['bundle']
    else:
        bundle = immutable["bundle"]

    if not 'storage' in immutable:
        storage = common_azure['storage']
    else:
        storage = immutable['storage']

    if not 'sg' in immutable:
        sg = common_azure['sg']
    else:
        sg = immutable['sg']

    # Login
    subprocess.call(["az", "login", "--service-principal", "-u", AZURE_CLIENT_ID, "-p", AZURE_SECRET, "--tenant", AZURE_TENANT])

    # Acquire credential object using CLI-based authentication.
    credential = AzureCliCredential()

    # Idempotence check if immutable name already exists
    check = check_immutable_by_name(immutable['name'])

    if (check):
        print(f"Immutable {immutable['name']} already exists, skip")
    else:

        # Get the management object for the network
        network_client = NetworkManagementClient(credential, AZURE_SUBSCRIPTION_ID)

        IP_NAME = str(immutable['name']) + "-ip"

        # Create the IP address
        poller = network_client.public_ip_addresses.begin_create_or_update(AZURE_RESOURCE_GROUP, IP_NAME,
                                                                           {
                                                                               "location": region,
                                                                               "sku": {"name": "Standard"},
                                                                               "public_ip_allocation_method": "Static",
                                                                               "public_ip_address_version": "IPV4"
                                                                           }
                                                                           )

        ip_address_result = poller.result()

        print(f"Provisioned public IP address {ip_address_result.name} with address {ip_address_result.ip_address}")


        IP_CONFIG_NAME = str(immutable['name']) + "-ip-config"
        NIC_NAME = str(immutable['name']) + "-nic"

        subnet_id = "/subscriptions/{0}/resourceGroups/{1}/providers/Microsoft.Network/virtualNetworks/{2}/subnets/{3}".format(AZURE_SUBSCRIPTION_ID, AZURE_RESOURCE_GROUP, vpc, subnet)

        # Network security group data
        sg_data = find_security_group(sg)

        # Create the network interface client
        poller = network_client.network_interfaces.begin_create_or_update(AZURE_RESOURCE_GROUP, NIC_NAME,
                                                                          {
                                                                              "location": region,
                                                                              "ip_configurations": [{
                                                                                  "name": IP_CONFIG_NAME,
                                                                                  "subnet": {"id": subnet_id},
                                                                                  "private_ip_allocation_method": "static",
                                                                                  #"private_ip_address_version": "ipv4",
                                                                                  "private_ip_address": immutable['private_ip_address'],
                                                                                  "public_ip_address": {
                                                                                      "id": ip_address_result.id
                                                                                  }
                                                                              }],
                                                                              'network_security_group': {
                                                                                  'id': sg_data['id']
                                                                              }
                                                                          }
                                                                          )

        nic_result = poller.result()

        print(f"Provisioned network interface client {nic_result.name}")


        # Create the virtual machine

        # Get the management object for virtual machines
        compute_client = ComputeManagementClient(credential, AZURE_SUBSCRIPTION_ID)

        VM_NAME = immutable['name']

        # TODO: this not permit standalone usage
        USERNAME = common['cm']['name']
        PUBKEY = common['cm']['pubkey']

        print(f"Provisioning virtual machine {VM_NAME}; this operation might take a few minutes.")

        # Create the VM
        poller = compute_client.virtual_machines.begin_create_or_update(AZURE_RESOURCE_GROUP, VM_NAME,
                                                                        {
                                                                            "location": region,
                                                                            "storage_profile": {
                                                                                #"image_reference": {
                                                                                #    "publisher": 'Canonical',
                                                                                #    "offer": "UbuntuServer",
                                                                                #    "sku": "18.04-LTS",
                                                                                #    "version": "latest"
                                                                                #}
                                                                                "image_reference": {
                                                                                    "id": "/subscriptions/{0}/resourceGroups/{1}/providers/Microsoft.Compute/images/{2}".format(AZURE_SUBSCRIPTION_ID, AZURE_RESOURCE_GROUP, blueprint)
                                                                                }
                                                                            },
                                                                            "hardware_profile": {
                                                                                "vm_size": bundle
                                                                            },
                                                                            "os_profile": {
                                                                                "computer_name": VM_NAME,
                                                                                "admin_username": USERNAME,
                                                                                "linux_configuration": {
                                                                                    "disable_password_authentication": True,
                                                                                    "ssh": {
                                                                                        "public_keys": [{
                                                                                            "path": "/home/{}/.ssh/authorized_keys".format(USERNAME),
                                                                                            "key_data": PUBKEY
                                                                                        }]
                                                                                    }
                                                                                }
                                                                            },
                                                                            "network_profile": {
                                                                                "network_interfaces": [{
                                                                                    "id": nic_result.id,
                                                                                }]
                                                                            }
                                                                        }
                                                                        )

        vm_result = poller.result()
        print(f"Provisioned virtual machine {vm_result.name}")


# Create azure immutable by powershell
def create_azure_powershell(immutable):

    print("creating")

    if not os.path.exists(BUILD_DIRECTORY):
        os.makedirs(BUILD_DIRECTORY)

    workspace = build_workspace(immutable['name'])

    if not os.path.exists(BUILD_DIRECTORY + '/' + workspace):
        os.makedirs(BUILD_DIRECTORY + '/' + workspace)

    subprocess.call('cp '+DEPLOY_TEMPLATE+' '+BUILD_DIRECTORY + '/' + workspace+'/deploy.template.ps1', shell=True)

    common = helpers.common()
    common_azure = common['defaults']['immutables']['azure']
    current_template = BUILD_DIRECTORY + '/' + workspace + '/deploy.template.ps1'
    current_script = BUILD_DIRECTORY + '/' + workspace + '/deploy.ps1'

    # Filter common values
    if not 'rg' in immutable:
        rg = common_azure['rg']
    else:
        rg = immutable["rg"]

    if not 'region' in immutable:
        region = common_azure['region']
    else:
        region = immutable["region"]

    if not 'vpc' in immutable:
        vpc = common_azure['vpc']
    else:
        vpc = immutable["vpc"]

    if not 'subnet' in immutable:
        subnet = common_azure['subnet']
    else:
        subnet = immutable["subnet"]

    if not 'blueprint' in immutable:
        blueprint = common_azure['blueprint']
    else:
        blueprint = immutable["blueprint"]

    if not 'bundle' in immutable:
        bundle = common_azure['bundle']
    else:
        bundle = immutable["bundle"]

    if not 'storage' in immutable:
        storage = common_azure['storage']
    else:
        storage = immutable['storage']

    # Inject values
    with open(current_template, 'r') as template:
        with open(current_script, 'w+') as output:
            for line in template.readlines():
                line = line.replace("@@IMMUTABLE_RESOURCE_GROUP@@", rg)
                line = line.replace("@@IMMUTABLE_REGION@@", region)
                line = line.replace("@@IMMUTABLE_VPC@@", vpc)
                line = line.replace("@@IMMUTABLE_SUBNET@@", subnet)
                line = line.replace("@@IMMUTABLE_NAME@@", immutable["name"])
                line = line.replace("@@IMMUTABLE_BLUEPRINT@@", blueprint)
                line = line.replace("@@IMMUTABLE_BUNDLE@@", bundle)
                line = line.replace("@@IMMUTABLE_STORAGE_ACCOUNT_TYPE@@", storage)
                line = line.replace("@@IMMUTABLE_PRIVATE_IP_ADDRESS@@", immutable["private_ip_address"])

                output.write(line)

    # Execute powershell script
    stdout = subprocess.check_output(['pwsh', BUILD_DIRECTORY + '/' + workspace + '/deploy.ps1'], universal_newlines=True)
    print(stdout)

# Multi process azure immutables
def process_azure(azure_immutables):
    with concurrent.futures.ProcessPoolExecutor() as executor:
        results = [executor.submit(create_azure, immutable) for immutable in azure_immutables]

        for f in concurrent.futures.as_completed(results):
            f.result()


# Main
def main():
    data = helpers.app()

    azure_immutables = []

    for k,immutable in data['immutables'].items():

        # Filter azure deployments
        if (immutable['type'] == "azure"):
            azure_immutables.append(immutable)


    t1 = threading.Thread(target=process_azure, args=[azure_immutables])

    t1.start()

    t1.join()



# Execute
if __name__ == '__main__':
    main()