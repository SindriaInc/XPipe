#!/usr/bin/env python3

import sys
import yaml
import subprocess

# Global roles config
ROLES_CONFIG = 'main.yml'

def roles():
  data = load(ROLES_CONFIG)
  roles = data[0]['roles']
  return roles

def load(file):
  with open(file, 'r') as f:
    try:
      data = yaml.load(f, Loader=yaml.FullLoader)
    except yaml.YAMLError as e:
      print(e)
      sys.exit(1)

  return data


def main():
  global roles

  protocol = 'https://'
  namespace = 'github.com/SindriaInc/'
  url = protocol + namespace

  roles = roles()

  for role in roles:
      print("Installing " + role + "...")
      subprocess.call(['git', 'clone', url + role + '.git', 'roles/' + role])
      print("Cleaning " + role + "...")
      subprocess.call(['rm', '-Rf', 'roles/' + role + '/.git'])
      subprocess.call(['rm', '-Rf', 'roles/' + role + '/.gitignore'])
      subprocess.call(['rm', '-Rf', 'roles/' + role + '/.env.local'])
      subprocess.call(['rm', '-Rf', 'roles/' + role + '/docker-compose.local.yml'])

  print("Done")

if __name__ == '__main__':
    main()