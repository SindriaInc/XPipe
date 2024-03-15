#!/usr/bin/env python3

import sys
import yaml
import subprocess

# Global modules config
MODULES_CONFIG = 'modules.yml'

def modules():
  data = load(MODULES_CONFIG)
  modules = data['modules']
  return modules

def load(file):
  with open(file, 'r') as f:
    try:
      data = yaml.load(f, Loader=yaml.FullLoader)
    except yaml.YAMLError as e:
      print(e)
      sys.exit(1)

  return data

def main():
  global modules

  protocol = 'https://'
  namespace = 'github.com/SindriaInc/'
  url = protocol + namespace

  modules = modules()

  for module in modules:
      print("Installing " + module + "...")
      subprocess.call(['git', 'clone', url + module + '.git', 'modules/' + module])
      print("Cleaning " + module + "...")
      subprocess.call(['rm', '-Rf', 'modules/' + module + '/.git'])
      subprocess.call(['rm', '-Rf', 'modules/' + module + '/.gitignore'])

  print("Done")

if __name__ == '__main__':
    main()