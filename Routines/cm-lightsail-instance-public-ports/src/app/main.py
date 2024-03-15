#!/usr/bin/env python3

import os

import helpers
import service


# Pycharm pydevd
PYCHARM_PYDEVD_ENABLED = int(os.getenv('PYCHARM_PYDEVD_ENABLED'))
PYCHARM_PYDEVD_HOST = str(os.getenv('PYCHARM_PYDEVD_HOST'))
PYCHARM_PYDEVD_PORT = int(os.getenv('PYCHARM_PYDEVD_PORT'))

# Enable pydevd debugger
if (PYCHARM_PYDEVD_ENABLED):
    import pydevd_pycharm
    pydevd_pycharm.settrace(PYCHARM_PYDEVD_HOST, port=PYCHARM_PYDEVD_PORT, stdoutToServer=True, stderrToServer=True)

# Core rules processing
def process_rules(name, rules):

    current_rules = service.get_rules(name)
    i = 0

    for rule in rules:

        j = 0

        for entry in current_rules['portStates']:

            if (rule['port_info']['fromPort'] == entry['fromPort']):

                # TODO: implement idempotence check - skip delete if existing rule is identically

                print("found, deleting...")
                service.delete_rule(name, rule)

                if (service.check_rule_cidrs(rule)):
                    service.create_rule_with_cidrs(name, rule)
                else:
                    service.create_rule(name, rule)
            else:
                print("not found, skipping...")

            j += 1

        i += 1

        if (service.check_rule_cidrs(rule)):
            service.create_rule_with_cidrs(name, rule)
        else:
            service.create_rule(name, rule)


# Main
def main():
    data = helpers.app()

    # Process lightsail instances
    for k,instance in data['lightsail'].items():
        process_rules(instance['name'], instance['rules'])


# Execute
if __name__ == '__main__':
    main()