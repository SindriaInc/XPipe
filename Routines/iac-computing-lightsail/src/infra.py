#!/usr/bin/env python3

import sys
import yaml
import json

def load(file):
  with open(file, 'r') as f:
    try:
      data = json.load(f)
    except json.JSONDecodeError as e:
      print(e)
      sys.exit(1)
  return data


def save(file, data):
    with open(file, 'w') as f:
        try:
            json.dump(data, f)
        except yaml.YAMLError as e:
            print(e)
            sys.exit(1)
    return True


def generate_resource_collection(resource):
    #print(resource)
    return resource


def main():
    output = load('output.json')
    resources = output['values']['outputs']['resources']

    blueprints = generate_resource_collection(resources['value']['blueprints'][0])
    instances = generate_resource_collection(resources['value']['instances'][0])
    nodes = generate_resource_collection(resources['value']['nodes'][0])


    infra = {
        'infra': {
            'blueprints': blueprints,
            'instances': instances,
            'nodes': nodes,
        }
    }

    save('infra.json', infra)


if __name__ == '__main__':
    main()