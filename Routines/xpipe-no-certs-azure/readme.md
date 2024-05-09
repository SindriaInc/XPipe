# Build Certs - Azure

This IaC component permit build SSL/TLS certs with azure dns.

This is atomic routine for any CI/CD such as gitlab-ci, bitbucket-pipelines etc.
In any case is possible to use it manually with run.sh helper script.

## Configuration

List of required envs. This envs will be set on your pipeline variables or in your .env file for manual usage.

| Key                                | Value                         |
| -----------------------------------|:-----------------------------:|
| AZURE_SUBSCRIPTION_ID              | <SubscriptionID>              |
| AZURE_CLIENT_ID                    | <ApplicationId>               |
| AZURE_SECRET                       | <Password>                    |
| AZURE_TENANT                       | <TenantID>                    |
| AZURE_RESOURCE_GROUP               | <ResourceGroupName>           |
| AZURE_STORAGE_ACCOUNT              | <StorageAccountName>          |
| AZURE_STORAGE_ACCESS_KEY           | <value>                       |
| AZURE_STORAGE_CONNECTION_STRING    | <value>                       |
| AZURE_CONF                         | base64 azure ini              |
| IAC_MODE                           | standalone or void            |
| IAC_CERTBOT_CACHE                  | example-certbot-cache         |
| IAC_CERTBOT_EMAIL                  | devops@example.com            |
| IAC_CERTBOT_DOMAIN                 | example.com                   |

### Azure ini example

```
dns_azure_sp_client_id = 912ce44a-0156-4669-ae22-c16a17d34ca5
dns_azure_sp_client_secret = E-xqXU83Y-jzTI6xe9fs2YC~mck3ZzUih9
dns_azure_tenant_id = ed1090f3-ab18-4b12-816c-599af8a88cf7

dns_azure_zone1 = example.com:/subscriptions/c135abce-d87d-48df-936c-15596c6968a5/resourceGroups/dns1
dns_azure_zone2 = example.org:/subscriptions/99800903-fb14-4992-9aff-12eaf2744622/resourceGroups/dns2
```

## Usage

Paste this command in your pipeline step:

`docker run --rm -t --env IAC_MODE=${IAC_MODE} --env AZURE_SUBSCRIPTION_ID=${AZURE_SUBSCRIPTION_ID} --env AZURE_CLIENT_ID=${AZURE_CLIENT_ID} --env AZURE_SECRET=${AZURE_SECRET} --env AZURE_TENANT=${AZURE_TENANT} --env AZURE_RESOURCE_GROUP=${AZURE_RESOURCE_GROUP} --env AZURE_STORAGE_ACCOUNT=${AZURE_STORAGE_ACCOUNT} --env AZURE_STORAGE_ACCESS_KEY=${AZURE_STORAGE_ACCESS_KEY} --env AZURE_STORAGE_CONNECTION_STRING=${AZURE_STORAGE_CONNECTION_STRING} --env AZURE_CONF=${AZURE_CONF} --env IAC_CERTBOT_CACHE=${IAC_CERTBOT_CACHE} --env IAC_CERTBOT_EMAIL=${IAC_CERTBOT_EMAIL} --env IAC_CERTBOT_DOMAIN=${IAC_CERTBOT_DOMAIN} sindriainc/build-certs-azure:1.0.0`

OR use the helper script:

`bash run.sh`

IMPORTANT: `Remeber to set all envs in your .env file before run.`

### Tips and Tricks

For standalone usage you can use certbot cache with volume. Append this to command above:

`-v ./letsencrypt:/etc/letsencrypt`


## Setup Development Environment

- Clone this repo: `git clone git@github.com:SindriaInc/build-certs-azure.git`
- Move into it: `cd build-certs-azure`
- Build local image: `bash build.sh sindriainc/build-certs-azure local`
- Setup env: `cp .env.local .env`
- Setup docker compose: `cp docker-compose.local.yml docker-compose.yml`
- Start environment: `docker-compose up -d`

## Links

- [https://certbot-dns-azure.readthedocs.io/en/latest/](https://certbot-dns-azure.readthedocs.io/en/latest/)