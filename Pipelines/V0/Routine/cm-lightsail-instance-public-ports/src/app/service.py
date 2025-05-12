import subprocess
import json

import helpers

# Get all rules of instance - return dict
def get_rules(name):
    stdout = subprocess.check_output(['aws', 'lightsail', 'get-instance-port-states', '--instance-name', name], universal_newlines=True)
    data = json.loads(stdout)
    return data

# Create a rule - return void
def create_rule(name, rule):
    subprocess.call(['aws', 'lightsail', 'open-instance-public-ports', '--instance-name', name, '--port-info', 'fromPort='+str(rule['port_info']['fromPort'])+',protocol='+str(rule['port_info']['protocol'])+',toPort='+str(rule['port_info']['toPort'])+''])

# Create a rule with restricted cidrs - return void
def create_rule_with_cidrs(name, rule):
    cidrs = str(helpers.build_cidrs(rule['port_info']['cidrs']))
    subprocess.call(['aws', 'lightsail', 'open-instance-public-ports', '--instance-name', name, '--port-info', 'fromPort='+str(rule['port_info']['fromPort'])+',protocol='+str(rule['port_info']['protocol'])+',toPort='+str(rule['port_info']['toPort'])+',cidrs='+cidrs+''])

# Delete existing rule - return void
def delete_rule(name, rule):
    subprocess.call(['aws', 'lightsail', 'close-instance-public-ports', '--instance-name', name, '--port-info', 'fromPort='+str(rule['port_info']['fromPort'])+',protocol='+str(rule['port_info']['protocol'])+',toPort='+str(rule['port_info']['toPort'])+''])

# Check if rule contain restricted cidrs - return boolean
def check_rule_cidrs(rule):
    if ('cidrs' in rule['port_info']):
        if (not rule['port_info']['cidrs']):
            return False
        else:
            return True
    return False