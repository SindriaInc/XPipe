#!/usr/bin/env python3

import os
import sys
import yaml
import subprocess
import datetime

from slugify import slugify

# Azure
AZURE_SUBSCRIPTION_ID = os.environ["AZURE_SUBSCRIPTION_ID"]
AZURE_CLIENT_ID = os.environ["AZURE_CLIENT_ID"]
AZURE_SECRET = os.environ["AZURE_SECRET"]
AZURE_TENANT = os.environ["AZURE_TENANT"]
AZURE_RESOURCE_GROUP = os.environ["AZURE_RESOURCE_GROUP"]
AZURE_STORAGE_ACCOUNT = os.environ["AZURE_STORAGE_ACCOUNT"]
AZURE_STORAGE_ACCESS_KEY = os.environ["AZURE_STORAGE_ACCESS_KEY"]
AZURE_STORAGE_CONNECTION_STRING = os.environ["AZURE_STORAGE_CONNECTION_STRING"]

# Git
IAC_GIT_PROTOCOL = os.environ["IAC_GIT_PROTOCOL"]
IAC_GIT_PROVIDER = os.environ["IAC_GIT_PROVIDER"]
IAC_GIT_NAMESPACE = os.environ["IAC_GIT_NAMESPACE"]
IAC_GIT_USERNAME = os.environ["IAC_GIT_USERNAME"]
IAC_GIT_PASSWORD = os.environ["IAC_GIT_PASSWORD"]
IAC_GIT_TEMPLATE = os.environ["IAC_GIT_TEMPLATE"]

# System
BASE_PATH = os.environ["BASE_PATH"]
IAC_REMOTE_USER = os.environ["IAC_REMOTE_USER"]
IAC_PRIVATE_KEY = os.environ["IAC_PRIVATE_KEY"]

# Certbot
IAC_CERTBOT_DOMAIN = os.environ["IAC_CERTBOT_DOMAIN"]
IAC_CERTBOT_EMAIL = os.environ["IAC_CERTBOT_EMAIL"]
IAC_CERTBOT_CACHE = os.environ["IAC_CERTBOT_CACHE"]
AZURE_CONF = os.environ["AZURE_CONF"]

# Docker
DOCKERHUB_NAMESPACE = os.environ["DOCKERHUB_NAMESPACE"]
DOCKERHUB_USERNAME = os.environ["DOCKERHUB_USERNAME"]
DOCKERHUB_PASSWORD = os.environ["DOCKERHUB_PASSWORD"]

# Orchestrator
IAC_ORCHESTRATOR_CACHE = os.environ["IAC_ORCHESTRATOR_CACHE"]
PIPELINES_FILE = 'pipelines.yaml'

# CYR
CYR_COMPANY_NAME = os.environ["CYR_COMPANY_NAME"]
CYR_PIVA = os.environ["CYR_PIVA"]
CYR_WEBURL = os.environ["CYR_WEBURL"]
CYR_REFERENTE = os.environ["CYR_REFERENTE"]
CYR_REFERENTEMAIL = os.environ["CYR_REFERENTEMAIL"]
CYR_REFERENTEPHONE = os.environ["CYR_REFERENTEPHONE"]
CYR_PACKAGE = os.environ["CYR_PACKAGE"]


##### Helpers #####

# Get current datetime - return string
def now():
  current = datetime.datetime.now()
  now = current.strftime("%Y-%m-%d %H:%M:%S")
  return now

# Load yaml file - return dict
def load(file):
  with open(file, 'r') as f:
    try:
      data = yaml.load(f, Loader=yaml.FullLoader)
    except yaml.YAMLError as e:
      print(e)
      sys.exit(1)
  return data

# Save yaml file - return boolean
def save(file, data):
  with open(file, 'w') as f:
    try:
      yaml.dump(data, f)
    except yaml.YAMLError as e:
      print(e)
      sys.exit(1)
  return True

##### End Helpers #####


##### Services #####

# Bootstrap orchestrator cache only if empty - return void
def bootstrap_cache():

    data = {
        'version': '0.1.0',
        'pipelines': {}
    }

    save(PIPELINES_FILE, data)

# Init orchestator cache - return void
def init_orchestrator_cache(cachedir):
    subprocess.call(["az", "storage", "blob", "download-batch", '-d', cachedir, '-s', IAC_ORCHESTRATOR_CACHE])

    # Check if orchestrator cache is empty
    if os.path.exists(cachedir) and os.path.isdir(cachedir):
        if not os.listdir(cachedir):
            print("Cache is empty")
            print()
            print("Bootstrap cache...")
            subprocess.call(['touch', cachedir + '/' + PIPELINES_FILE])
            bootstrap_cache()
            print()
        else:
            print("Cache already exists, continue...")
            print()
    else:
        print("Given Directory don't exists")
        sys.exit(1)

# Update orchestrator cache - return void
def update_orchestrator_cache(cachedir):
    subprocess.call(["az", "storage", "blob", "sync", '-s', cachedir, '-c', IAC_ORCHESTRATOR_CACHE])

# Generate company name unique slug - return string
def generate_company_name_slug():
    return slugify(CYR_COMPANY_NAME).lower()

# Check if pipeline already exists - return boolean
def check_if_pipeline_exists(company_name_slug, cached_pipelines):

    if not cached_pipelines['pipelines']:
        return False

    for pipeline in cached_pipelines['pipelines']:
        if (pipeline['company_name'] == company_name_slug):
            return True
    return False

##### End Services #####

def create_new_entry():
    pass

# Main
def main():
    workdir = 'tmp_workdir'
    cachedir = 'cache_dir'

    print("Cyberefund Orchestator")
    print()

    print("Preparing workdir...")
    subprocess.call(['mkdir', '-p', workdir])
    print()

    print("Preparing cachedir...")
    subprocess.call(['mkdir', '-p', cachedir])
    print()

    # Login
    subprocess.call(["az", "login", "--service-principal", "-u", AZURE_CLIENT_ID, "-p", AZURE_SECRET, "--tenant", AZURE_TENANT])

    print("Init cache...")
    init_orchestrator_cache(cachedir)

    company_name_slug = generate_company_name_slug()

    cached_pipelines = load(cachedir + '/' + PIPELINES_FILE)

    print(cached_pipelines)

    print(type(cached_pipelines))

    sys.exit(0)

    check = check_if_pipeline_exists(company_name_slug, cached_pipelines)

    print(check)

    sys.exit(0)

    if (check):
        pass



    print("Cloning template " + IAC_GIT_TEMPLATE + "...")
    subprocess.call(['git', 'clone',IAC_GIT_PROTOCOL + IAC_GIT_USERNAME + ':' + IAC_GIT_PASSWORD + '@' + IAC_GIT_PROVIDER + '/' + IAC_GIT_NAMESPACE + '/' + IAC_GIT_TEMPLATE + '.git', workdir + '/' + IAC_GIT_TEMPLATE])
    print()


    print("Update cache...")
    update_orchestrator_cache(cachedir)



# Execute
if __name__ == '__main__':
    main()