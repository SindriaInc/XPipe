import os
import subprocess
import json

AZURE_SUBSCRIPTION_ID = os.environ["AZURE_SUBSCRIPTION_ID"]
AZURE_CLIENT_ID = os.environ["AZURE_CLIENT_ID"]
AZURE_SECRET = os.environ["AZURE_SECRET"]
AZURE_TENANT = os.environ["AZURE_TENANT"]
AZURE_RESOURCE_GROUP = os.environ["AZURE_RESOURCE_GROUP"]
AZURE_STORAGE_ACCOUNT = os.environ["AZURE_STORAGE_ACCOUNT"]
AZURE_STORAGE_ACCESS_KEY = os.environ["AZURE_STORAGE_ACCESS_KEY"]
AZURE_STORAGE_CONNECTION_STRING = os.environ["AZURE_STORAGE_CONNECTION_STRING"]

# Check if vm_name key exist on entry - return boolean
def check_vm_name(entry):
    if not 'vm_name' in entry:
        return False
    return True

# Find vm public ip - return string or null
def find_public_ip_address_by_name(name):
    query = "[?name=='{0}']".format(name)
    stdout = subprocess.check_output(['az', 'vm', 'list', '-d', '-o', 'json', '--query', query], universal_newlines=True)
    data = json.loads(stdout)

    if data:
        return data[0]['publicIps']
    return None