# Publish Image

This IaC component permit build and publish every docker image.

This is atomic routine for any CI/CD such as gitlab-ci, bitbucket-pipelines etc.
In any case is possible to use it manually with run.sh helper script.

## Configuration

List of required envs. This envs will be set on your pipeline variables or in your .env file for manual usage.

| Key                       | Value                         |
| --------------------------|:-----------------------------:|
| DOCKERHUB_NAMESPACE       | example                       |
| DOCKERHUB_USERNAME        | service account username      |
| DOCKERHUB_PASSWORD        | secret                        |
| IAC_MODE                  | standalone or void            |
| IAC_GIT_USERNAME          | service account username      |
| IAC_GIT_PASSWORD          | secret                        |
| IAC_GIT_PROVIDER          | provider-fqdn                 |
| IAC_GIT_NAMESPACE         | repo-namespace                |
| IAC_APP_NAME              | repo-slug                     |
| IAC_APP_VERSION           | tag version or branch name    |


## Usage

Paste this command in your pipeline step:

`docker run --rm -t --env IAC_MODE=${IAC_MODE} --env DOCKERHUB_NAMESPACE=${DOCKERHUB_NAMESPACE} --env DOCKERHUB_USERNAME=${DOCKERHUB_USERNAME} --env DOCKERHUB_PASSWORD=${DOCKERHUB_PASSWORD} --env IAC_GIT_USERNAME=${IAC_GIT_USERNAME} --env IAC_GIT_PASSWORD=${IAC_GIT_PASSWORD} --env IAC_GIT_PROVIDER=${IAC_GIT_PROVIDER} --env IAC_GIT_NAMESPACE=${IAC_GIT_NAMESPACE} --env IAC_APP_NAME=${IAC_APP_NAME} --env IAC_APP_VERSION=${IAC_APP_VERSION} sindriainc/publish-image:1.0.0`

OR use the helper script:

`bash run.sh`

IMPORTANT: `Remeber to set all envs in your .env file before run.`

### Tips and Tricks

For standalone usage you can use certbot cache with volume. Append this to command above:

`-v ./:/home/sindria/.build`


## Setup Development Environment

- Clone this repo: `git clone git@github.com:SindriaInc/publish-image.git`
- Move into it: `cd publish-image`
- Build local image: `bash build.sh sindriainc/publish-image local`
- Setup env: `cp .env.local .env`
- Setup docker compose: `cp docker-compose.local.yml docker-compose.yml`
- Start environment: `docker-compose up -d`