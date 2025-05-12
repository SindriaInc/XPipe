# IaC DNS Azure

This IaC component permit parallel setup of dns resources on azure cloud.

This is atomic routine for any CI/CD such as gitlab-ci, bitbucket-pipelines etc.
In any case is possible to use it manually with run.sh helper script.

## Configuration

List of required envs. This envs will be set on your pipeline variables or in your .env file for manual usage.

| Key                                | Value                       |
| -----------------------------------|:---------------------------:|
| AZURE_SUBSCRIPTION_ID              | SubscriptionID              |
| AZURE_CLIENT_ID                    | ApplicationId               |
| AZURE_SECRET                       | Password                    |
| AZURE_TENANT                       | TenantID                    |
| AZURE_RESOURCE_GROUP               | ResourceGroupName           |
| AZURE_STORAGE_ACCOUNT              | StorageAccountName          |
| AZURE_STORAGE_ACCESS_KEY           | value                       |
| AZURE_STORAGE_CONNECTION_STRING    | value                       |
| IAC_MODE                           | standalone or void          |
| IAC_GIT_USERNAME                   | service-account-username    |
| IAC_GIT_PASSWORD                   | service-account-username    |
| IAC_GIT_PROVIDER                   | provider-fqdn               |
| IAC_GIT_NAMESPACE                  | repo-namespace              |
| IAC_INFRA_NAME                     | repo-slug                   |
| IAC_LIVE_CACHE                     | bucket-name                 |
| IAC_CURRENT_INFRA                  | bucket-name                 |
| ENTRY_NAME                         | www                         |
| ENTRY_TYPE                         | A                           |
| ENTRY_TTL                          | 60                          |
| ENTRY_VALUE                        | 10.10.10.10                 |
| ENTRY_DOMAIN                       | example.com                 |
| ENTRY_RG                           | example-rg                  |
| VM_NAME                            | docker-node-01              |


## Usage

Paste this command in your pipeline step:

`docker run --rm -t --env IAC_MODE=${IAC_MODE} --env AZURE_SUBSCRIPTION_ID=${AZURE_SUBSCRIPTION_ID} --env AZURE_CLIENT_ID=${AZURE_CLIENT_ID} --env AZURE_SECRET=${AZURE_SECRET} --env AZURE_TENANT=${AZURE_TENANT} --env AZURE_RESOURCE_GROUP=${AZURE_RESOURCE_GROUP} --env AZURE_STORAGE_ACCOUNT=${AZURE_STORAGE_ACCOUNT} --env AZURE_STORAGE_ACCESS_KEY=${AZURE_STORAGE_ACCESS_KEY} --env AZURE_STORAGE_CONNECTION_STRING=${AZURE_STORAGE_CONNECTION_STRING} --env IAC_GIT_USERNAME=${IAC_GIT_USERNAME} --env IAC_GIT_PASSWORD=${IAC_GIT_PASSWORD} --env IAC_GIT_PROVIDER=${IAC_GIT_PROVIDER} --env IAC_GIT_NAMESPACE=${IAC_GIT_NAMESPACE} --env IAC_INFRA_NAME=${IAC_INFRA_NAME} --env IAC_LIVE_CACHE=${IAC_LIVE_CACHE} --env IAC_CURRENT_INFRA=${IAC_CURRENT_INFRA} --env ENTRY_NAME=${ENTRY_NAME} --env ENTRY_TYPE=${ENTRY_TYPE} --env ENTRY_TTL=${ENTRY_TTL} --env ENTRY_VALUE=${ENTRY_VALUE} --env ENTRY_DOMAIN=${ENTRY_DOMAIN} --env ENTRY_RG=${ENTRY_RG} --env VM_NAME=${VM_NAME} sindriainc/iac-dns-azure:1.0.0`

OR use the helper script:

`bash run.sh`

IMPORTANT: `Remeber to set all envs in your .env file before run.`

### Tips and Tricks

For standalone usage you can override deployments.yaml configuration with volume. Append this to command above:

`-v ./config:/var/www/app/config`


## Setup Development Environment

- Clone this repo: `git clone git@github.com:SindriaInc/iac-dns-azure.git`
- Move into it: `cd iac-dns-azure`
- Build local image: `bash build.sh sindriainc/iac-dns-azure local`
- Setup env: `cp .env.local .env`
- Setup docker compose: `cp docker-compose.local.yml docker-compose.yml`
- Start environment: `docker-compose up -d`