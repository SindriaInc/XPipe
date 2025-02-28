# CYR - Orchestrator

This IaC component permit orchestration of cyberefund obserr customers pipelines.

## Configuration

List of required envs. This envs will be set on your pipeline variables.

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
| IAC_GIT_PROTOCOL                   | https://                    |
| IAC_GIT_PROVIDER                   | bitbucket.org               |
| IAC_GIT_NAMESPACE                  | Cyberefund                  |
| IAC_GIT_USERNAME                   | service-account-username    |
| IAC_GIT_PASSWORD                   | service-account-app-password|
| IAC_GIT_TEMPLATE                   | pipeline-template-repo-slug |
| BASE_PATH                          | /home/cyberefund            |
| IAC_REMOTE_USER                    | cyberefund                  |
| IAC_PRIVATE_KEY                    | base64-private-key          |
| IAC_CERTBOT_DOMAIN                 | production-obserr.net       |
| IAC_CERTBOT_EMAIL                  | devops@cyberefund.com       |
| IAC_CERTBOT_CACHE                  | blob-storage-name           |
| AZURE_CONF                         | base64-azure-ini            |
| DOCKERHUB_NAMESPACE                | cyberefund                  |
| DOCKERHUB_USERNAME                 | cyberefunddev               |
| DOCKERHUB_PASSWORD                 | secret                      |
| IAC_ORCHESTRATOR_CACHE             | blob-storage-name           |
| CYR_COMPANY_NAME                   | Herzum S.r.l                |
| CYR_PIVA                           |                             |
| CYR_WEBURL                         |  https://herzum.com         |
| CYR_REFERENTE                      |  Matteo Silva               |
| CYR_REFERENTEMAIL                  |  matteo.silva@herzum.com    |
| CYR_REFERENTEPHONE                 |                             |
| CYR_PACKAGE                        |  10                         |


## Usage

Paste this command in your pipeline step:

`docker run --rm -t --env AZURE_SUBSCRIPTION_ID=${AZURE_SUBSCRIPTION_ID} --env AZURE_CLIENT_ID=${AZURE_CLIENT_ID} --env AZURE_SECRET=${AZURE_SECRET} --env AZURE_TENANT=${AZURE_TENANT} --env AZURE_RESOURCE_GROUP=${AZURE_RESOURCE_GROUP} --env AZURE_STORAGE_ACCOUNT=${AZURE_STORAGE_ACCOUNT} --env AZURE_STORAGE_ACCESS_KEY=${AZURE_STORAGE_ACCESS_KEY} --env AZURE_STORAGE_CONNECTION_STRING=${AZURE_STORAGE_CONNECTION_STRING} --env IAC_GIT_PROTOCOL=${IAC_GIT_PROTOCOL} --env IAC_GIT_PROVIDER=${IAC_GIT_PROVIDER} --env IAC_GIT_NAMESPACE=${IAC_GIT_NAMESPACE} --env IAC_GIT_USERNAME=${IAC_GIT_USERNAME} --env IAC_GIT_PASSWORD=${IAC_GIT_PASSWORD} --env IAC_GIT_TEMPLATE=${IAC_GIT_TEMPLATE} --env BASE_PATH=${BASE_PATH} --env IAC_REMOTE_USER=${IAC_REMOTE_USER} --env IAC_PRIVATE_KEY=${IAC_PRIVATE_KEY} --env IAC_CERTBOT_DOMAIN=${IAC_CERTBOT_DOMAIN} --env IAC_CERTBOT_EMAIL=${IAC_CERTBOT_EMAIL} --env IAC_CERTBOT_CACHE=${IAC_CERTBOT_CACHE} --env AZURE_CONF=${AZURE_CONF} --env DOCKERHUB_NAMESPACE=${DOCKERHUB_NAMESPACE} --env DOCKERHUB_USERNAME=${DOCKERHUB_USERNAME} --env DOCKERHUB_PASSWORD=${DOCKERHUB_PASSWORD} --env IAC_ORCHESTRATOR_CACHE=${IAC_ORCHESTRATOR_CACHE} --env CYR_COMPANY_NAME=${CYR_COMPANY_NAME} --env CYR_PIVA=${CYR_PIVA} --env CYR_WEBURL=${CYR_WEBURL} --env CYR_REFERENTE=${CYR_REFERENTE} --env CYR_REFERENTEMAIL=${CYR_REFERENTEMAIL} --env CYR_REFERENTEPHONE=${CYR_REFERENTEPHONE} --env CYR_PACKAGE=${CYR_PACKAGE} cyberefund/cyr-orchestrator:1.0.0`


## Setup Development Environment

- Build local image: `bash build.sh cyberefund/cyr-orchestrator local`
- Setup env: `cp .env.local .env`
- Setup docker compose: `cp docker-compose.local.yml docker-compose.yml`
- Start environment: `docker-compose up -d`