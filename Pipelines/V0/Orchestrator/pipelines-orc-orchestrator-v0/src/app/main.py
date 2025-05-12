#!/usr/bin/env python3

import os
import sys
import yaml
import subprocess
import datetime
import requests
import time

from slugify import slugify

# Global
BITBUCKET_API_URL = 'https://api.bitbucket.org'
BITBUCKET_API_VERSION = '2.0'
BITBUCKET_PROJECT = 'IAC'

WORKDIR = 'tmp_workdir'
CACHEDIR = 'cache_dir'
PIPELINES_FILE = 'pipelines.yaml'


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

    save(CACHEDIR + '/' + PIPELINES_FILE, data)

# Init orchestator cache - return void
def init_orchestrator_cache():
    subprocess.call(["az", "storage", "blob", "download-batch", '-d', CACHEDIR, '-s', IAC_ORCHESTRATOR_CACHE])

    # Check if orchestrator cache is empty
    if os.path.exists(CACHEDIR) and os.path.isdir(CACHEDIR):
        if not os.listdir(CACHEDIR):
            print("Cache is empty")
            print()
            print("Bootstrap cache...")
            subprocess.call(['touch', CACHEDIR + '/' + PIPELINES_FILE])
            bootstrap_cache()
            print()
        else:
            print("Cache already exists, continue...")
            print()
    else:
        print("Given Directory don't exists")
        sys.exit(1)

# Update orchestrator cache - return void
def update_orchestrator_cache():
    subprocess.call(["az", "storage", "blob", "sync", '-s', CACHEDIR, '-c', IAC_ORCHESTRATOR_CACHE])

# Generate company name unique slug - return string
def generate_company_name_slug():
    return slugify(CYR_COMPANY_NAME).lower()

# Check if pipeline already exists - return boolean
def check_if_pipeline_exists(company_name_slug, cached_pipelines):

    if not cached_pipelines['pipelines']:
        return False

    for k,pipeline in cached_pipelines['pipelines'].items():
        if (pipeline['company_name_slug'] == company_name_slug):
            return True
    return False

# Check if the pipeline repo exists - return boolean
def check_if_repo_exits(name):

    url = '{0}/{1}/repositories/{2}/{3}'.format(BITBUCKET_API_URL, BITBUCKET_API_VERSION, IAC_GIT_NAMESPACE, name)
    headers = {'Content-Type': 'application/json'}
    request = requests.get(url, headers=headers, auth=(IAC_GIT_USERNAME, IAC_GIT_PASSWORD))

    status = request.status_code
    response = request.json()

    if (status == 200):
        return True

    if (status == 404):
        return False

    return False

# Get pipelines list of given repo - return dict
def list_pipelines(name):

    # TODO: add try catch with timeout and error exit

    url = '{0}/{1}/repositories/{2}/{3}/pipelines/'.format(BITBUCKET_API_URL, BITBUCKET_API_VERSION, IAC_GIT_NAMESPACE, name)
    headers = {'Content-Type': 'application/json'}
    request = requests.get(url, headers=headers, auth=(IAC_GIT_USERNAME, IAC_GIT_PASSWORD))
    response = request.json()

    return response

# Find first pipeline uuid - return string
def find_first_pipeline_uuid(name):
    pipelines = list_pipelines(name)
    return pipelines['values'][0]['uuid']

# Stop first pipeline of given repo - return int
def stop_pipeline(name):

    uuid = find_first_pipeline_uuid(name)

    # TODO: add try catch with timeout and error exit

    url = '{0}/{1}/repositories/{2}/{3}/pipelines/{4}/stopPipeline'.format(BITBUCKET_API_URL, BITBUCKET_API_VERSION, IAC_GIT_NAMESPACE, name, uuid)
    headers = {'Content-Type': 'application/json'}
    request = requests.post(url, headers=headers, auth=(IAC_GIT_USERNAME, IAC_GIT_PASSWORD))
    return request.status_code

# Trigger a pipeline - return dict
def trigger_pipeline(name):

    payload = {
        "target": {
            "ref_type": "branch",
            "type": "pipeline_ref_target",
            "ref_name": "master"
        },
        "variables": [
            {
                "key": "CYR_COMPANY_NAME",
                "value": CYR_COMPANY_NAME,
                "secured": False
            },
            {
                "key": "CYR_PACKAGE",
                "value": CYR_PACKAGE,
                "secured": False
            },
            {
                "key": "CYR_PIVA",
                "value": CYR_PIVA,
                "secured": False
            },
            {
                "key": "CYR_WEBURL",
                "value": CYR_WEBURL,
                "secured": False
            },
            {
                "key": "CYR_REFERENTE",
                "value": CYR_REFERENTE,
                "secured": False
            },
            {
                "key": "CYR_REFERENTEMAIL",
                "value": CYR_REFERENTEMAIL,
                "secured": False
            },
            {
                "key": "CYR_REFERENTEPHONE",
                "value": CYR_REFERENTEPHONE,
                "secured": False
            }
        ]
    }

    # TODO: add try catch with timeout and error exit

    url = '{0}/{1}/repositories/{2}/{3}/pipelines/'.format(BITBUCKET_API_URL, BITBUCKET_API_VERSION, IAC_GIT_NAMESPACE, name)
    headers = {'Content-Type': 'application/json'}
    request = requests.post(url, headers=headers, json=payload, auth=(IAC_GIT_USERNAME, IAC_GIT_PASSWORD))
    response = request.json()

    return response

##### End Services #####

# Find entry data into cached pipelines - return dict
def find_entry(cached_pipelines, company_name_slug):

    data = None
    entries = cached_pipelines['pipelines']

    for k,entry in entries.items():
        if (entry['company_name_slug'] == company_name_slug):
            data = entry

    return data

# Create a new entry into cached pipelines - return dict
def create_new_entry(cached_pipelines, company_name_slug):

    cache_data = cached_pipelines['pipelines']

    i = 0
    for increment in cache_data:
        i += 1

    pipeline = 'customer-0' + str(i)

    entry = {
        'id': int(i),
        'name': pipeline,
        'company_name_slug': company_name_slug,
        'company_name': CYR_COMPANY_NAME,
        'piva': CYR_PIVA,
        'weburl': CYR_WEBURL,
        'referente': CYR_REFERENTE,
        'referente_email': CYR_REFERENTEMAIL,
        'referente_phone': CYR_REFERENTEPHONE,
        'package': int(CYR_PACKAGE),
    }

    cached_pipelines['pipelines'][int(i)] = entry
    save(CACHEDIR + '/' + PIPELINES_FILE, cached_pipelines)
    return entry

# Edit existing entry into cached pipelines - return dict
def edit_entry(cached_pipelines, company_name_slug):

    current_entry = find_entry(cached_pipelines, company_name_slug)

    updated_entry = {
        'id': int(current_entry['id']),
        'name': current_entry['name'],
        'company_name_slug': company_name_slug,
        'company_name': CYR_COMPANY_NAME,
        'piva': CYR_PIVA,
        'weburl': CYR_WEBURL,
        'referente': CYR_REFERENTE,
        'referente_email': CYR_REFERENTEMAIL,
        'referente_phone': CYR_REFERENTEPHONE,
        'package': int(CYR_PACKAGE),
    }

    cached_pipelines['pipelines'][int(current_entry['id'])] = updated_entry
    save(CACHEDIR + '/' + PIPELINES_FILE, cached_pipelines)
    return updated_entry

# Create new pipeline repo - return dict
def create_repo(name):

    payload = {
        "scm": "git",
        "is_private": True,
        "project": {
        "key": BITBUCKET_PROJECT
        }
    }

    # TODO: add try catch with timeout and error exit

    url = '{0}/{1}/repositories/{2}/{3}'.format(BITBUCKET_API_URL, BITBUCKET_API_VERSION, IAC_GIT_NAMESPACE, name)
    headers = {'Content-Type': 'application/json'}
    request = requests.post(url, headers=headers, json=payload, auth=(IAC_GIT_USERNAME, IAC_GIT_PASSWORD))
    response = request.json()

    return response

# Enable pipelines into repo - return dict
def enable_pipeline_repo(name):

    # payload = {
    #     "type": "<string>",
    #     "enabled": True,
    #     "repository": {
    #         "type": "<string>"
    #     }
    # }

    payload = {
        "enabled": True
    }

    # TODO: add try catch with timeout and error exit

    url = '{0}/{1}/repositories/{2}/{3}/pipelines_config'.format(BITBUCKET_API_URL, BITBUCKET_API_VERSION, IAC_GIT_NAMESPACE, name)
    headers = {
        "Accept": "application/json",
        "Content-Type": "application/json"
    }
    request = requests.put(url, headers=headers, json=payload, auth=(IAC_GIT_USERNAME, IAC_GIT_PASSWORD))
    response = request.json()

    return response

# Generate last decimal 8 bit of private ip address for 24 bit subnet - return int or null
def generate_ip_last_bits_optimized(id):
    if (id < 0):
        return None

    return id + 9

# Generate last decimal 8 bit of private ip address for 24 bit subnet - return int or null
def generate_ip_last_bits(id):
    result = None
    start_value = 9
    end_value = 199

    i = -1
    for value in range(start_value, end_value):
        i += 1

        if (int(id) == 0):
            result = start_value
        if (int(id) > 0):
            for item in range(i, end_value):
                if (int(id) == i):
                    result = value
        if (int(id) < 0):
            result = None

    return result

# Process deployments config from template
def process_deployments(entry):

    bundle = 'Standard_B4ms'

    if (int(CYR_PACKAGE) != 10):
        bundle = 'D8as_v4'

    private_ip_address_prefix = '10.127.0.'
    last_bits_decimal = generate_ip_last_bits(entry['id'])

    private_ip_address = private_ip_address_prefix + str(last_bits_decimal)

    current_template = WORKDIR + '/' + IAC_GIT_TEMPLATE + '/config/deployments.template.yaml'
    current_config = WORKDIR + '/' + IAC_GIT_TEMPLATE + '/config/deployments.yaml'

    # Inject values
    with open(current_template, 'r') as template:
        with open(current_config, 'w+') as output:
            for line in template.readlines():
                line = line.replace("@@IMMUTABLE_NAME@@", entry["name"])
                line = line.replace("@@IMMUTABLE_BUNDLE@@", bundle)
                line = line.replace("@@IMMUTABLE_PRIVATE_IP_ADDRESS@@", private_ip_address)

                output.write(line)

    print("Cleaning template...")
    subprocess.call(['rm', '-Rf', current_template])
    print()


# Process dns config from template
def process_dns(entry):

    current_template = WORKDIR + '/' + IAC_GIT_TEMPLATE + '/config/dns.template.yaml'
    current_config = WORKDIR + '/' + IAC_GIT_TEMPLATE + '/config/dns.yaml'

    # Inject values
    with open(current_template, 'r') as template:
        with open(current_config, 'w+') as output:
            for line in template.readlines():
                line = line.replace("@@IMMUTABLE_NAME@@", entry["name"])
                line = line.replace("@@DOMAIN@@", IAC_CERTBOT_DOMAIN)

                output.write(line)

    print("Cleaning template...")
    subprocess.call(['rm', '-Rf', current_template])
    print()

# Generate pipeline config data
def generate_pipeline_config(entry):
    process_deployments(entry)
    process_dns(entry)

def commit_and_push_repo(name):

    print("Commit")
    subprocess.call(['bash', 'bin/commit.sh', name])
    print()

    print("Push")
    subprocess.call(['git', 'push', IAC_GIT_PROTOCOL + IAC_GIT_USERNAME + ':' + IAC_GIT_PASSWORD + '@' + IAC_GIT_PROVIDER + '/' + IAC_GIT_NAMESPACE + '/' + name + '.git', '--all'])
    print()

    print("Changing directory...")
    os.chdir("/var/www/app")
    print()



# Main
def main():
    print("Cyberefund Orchestator")
    print()

    print("Preparing workdir...")
    subprocess.call(['mkdir', '-p', WORKDIR])
    print()

    print("Preparing cachedir...")
    subprocess.call(['mkdir', '-p', CACHEDIR])
    print()

    # Login
    subprocess.call(["az", "login", "--service-principal", "-u", AZURE_CLIENT_ID, "-p", AZURE_SECRET, "--tenant", AZURE_TENANT])

    print("Init cache...")
    init_orchestrator_cache()

    company_name_slug = generate_company_name_slug()

    cached_pipelines = load(CACHEDIR + '/' + PIPELINES_FILE)

    check = check_if_pipeline_exists(company_name_slug, cached_pipelines)


    if (check):
        print("Pipeline already created")
        print()

        print("Checking current pipeline data...")
        entry = find_entry(cached_pipelines, company_name_slug)
        #print(entry)

        if (int(entry['package']) == int(CYR_PACKAGE)):
            print("Pipeline package still the same, skip...")
            print()

            repo = check_if_repo_exits(entry['name'])

            if (repo):
                result = trigger_pipeline(entry['name'])
                print(result)
            else:
                # TODO: Implement repo regeneration (accidentally deleted)
                # Questo significa quando la pipeline e' presente nella cache (entry nello yaml), ha lo stesso package (solitamente 10 ossia soluzione Small), ma il suo repo e' stato elimitato.
                print("Repo da rigenerare")
                sys.exit(1)
        else:
            print("Pipeline package need to be updated, updating...")
            print()

            entry = edit_entry(cached_pipelines, company_name_slug)
            #print(entry)

            print("Cloning pipeline " + entry['name'] + "...")
            subprocess.call(['git', 'clone', IAC_GIT_PROTOCOL + IAC_GIT_USERNAME + ':' + IAC_GIT_PASSWORD + '@' + IAC_GIT_PROVIDER + '/' + IAC_GIT_NAMESPACE + '/' + entry['name'] + '.git', WORKDIR + '/' + IAC_GIT_TEMPLATE])
            print()

            # TODO: implement package update in deployments.yaml before push
            # Questo caso ha bassa priorita' e nelle prime versioni del prodotto puo' essere lasciato in TODO.

            print("Changing directory...")
            os.chdir(WORKDIR + '/' + IAC_GIT_TEMPLATE)
            print()

            commit_and_push_repo(entry['name'])

            repo = check_if_repo_exits(entry['name'])

            if (repo):
                result = trigger_pipeline(entry['name'])
                print(result)
            else:
                # TODO: Implement repo regeneration (accidentally deleted)
                # Questo significa quando la pipeline e' presente nella cache (entry nello yaml), ha un package diverso da quello iniziale (20 == soluzione Medium OPPURE 30 == soluzione Large), ma il suo repo e' stato elimitato.
                # Questo caso ha bassa priorita' e nelle prime versioni del prodotto puo' essere lasciato in TODO.
                print("Repo da rigenerare")
                sys.exit(1)
    else:
        print("Creating new pipeline...")
        entry = create_new_entry(cached_pipelines, company_name_slug)
        #print(entry)

        print("Cloning template " + IAC_GIT_TEMPLATE + "...")
        subprocess.call(['git', 'clone', IAC_GIT_PROTOCOL + IAC_GIT_USERNAME + ':' + IAC_GIT_PASSWORD + '@' + IAC_GIT_PROVIDER + '/' + IAC_GIT_NAMESPACE + '/' + IAC_GIT_TEMPLATE + '.git', WORKDIR + '/' + IAC_GIT_TEMPLATE])
        print()

        print("Cleaning template " + IAC_GIT_TEMPLATE + "...")
        subprocess.call(['rm', '-Rf', WORKDIR + '/' + IAC_GIT_TEMPLATE + '/.git'])
        print()

        repo = create_repo(entry['name'])
        #print(repo)

        enable = enable_pipeline_repo(entry['name'])
        # print(enable)

        generate_pipeline_config(entry)

        print("Changing directory...")
        os.chdir(WORKDIR + '/' + IAC_GIT_TEMPLATE)
        print()

        print("Init Repo...")
        subprocess.call(['git', 'init'])
        print()

        commit_and_push_repo(entry['name'])

        print("Stopping init pipeline...")
        time.sleep(10)
        stop_pipeline(entry['name'])
        time.sleep(5)
        print()

        print("Stopped")
        print()

        print("Trigger new pipeline...")
        result = trigger_pipeline(entry['name'])
        print(result)


    #subprocess.call(['pwd'])

    print("Update cache...")
    update_orchestrator_cache()
    print()

    print("Cleaning...")
    subprocess.call(['rm', '-Rf', WORKDIR])
    subprocess.call(['rm', '-Rf', CACHEDIR])
    print()

    print("Success")


# Execute
if __name__ == '__main__':
    main()