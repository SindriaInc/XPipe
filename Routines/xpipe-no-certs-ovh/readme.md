# Build Certs - OVH

This IaC component permit build SSL/TLS certs with ovh dns.

This is atomic routine for any CI/CD such as gitlab-ci, bitbucket-pipelines etc.
In any case is possible to use it manually with run.sh helper script.

## Configuration

List of required envs. This envs will be set on your pipeline variables or in your .env file for manual usage.

| Key                       | Value                         |
| --------------------------|:-----------------------------:|
| AWS_ACCESS_KEY_ID         | <access-key>                  |
| AWS_SECRET_ACCESS_KEY     | <secret-key>                  |
| AWS_DEFAULT_REGION        | eu-central-1                  |
| OVH_CONF                  | base64 ovh ini secrets        |
| IAC_MODE                  | standalone or void            |
| IAC_CERTBOT_CACHE         | example-certbot-cache         |
| IAC_CERTBOT_EMAIL         | devops@example.com            |
| IAC_CERTBOT_DOMAIN        | example.com                   |


## Usage

Paste this command in your pipeline step:

`docker run --rm -t --env IAC_MODE=${IAC_MODE} --env AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID} --env AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY} --env AWS_DEFAULT_REGION=${AWS_DEFAULT_REGION} --env OVH_CONF=${OVH_CONF} --env IAC_CERTBOT_CACHE=${IAC_CERTBOT_CACHE} --env IAC_CERTBOT_EMAIL=${IAC_CERTBOT_EMAIL} --env IAC_CERTBOT_DOMAIN=${IAC_CERTBOT_DOMAIN} sindriainc/build-certs-ovh:1.0.0`

OR use the helper script:

`bash run.sh`

IMPORTANT: `Remeber to set all envs in your .env file before run.`

### Tips and Tricks

For standalone usage you can use certbot cache with volume. Append this to command above:

`-v ./letsencrypt:/etc/letsencrypt`


## Setup Development Environment

- Clone this repo: `git clone git@github.com:SindriaInc/build-certs-ovh.git`
- Move into it: `cd build-certs-ovh`
- Build local image: `bash build.sh sindriainc/build-certs-ovh local`
- Setup env: `cp .env.local .env`
- Setup docker compose: `cp docker-compose.local.yml docker-compose.yml`
- Start environment: `docker-compose up -d`