# Zabbix Deployment

Zabbix deployment ready for production and local environments.

## Deploy production environment

- Create AWS Postgres RDS instance
- Create AWS docker node instance
- Clone this repo on docker node: `git clone git@github.com:SindriaInc/zabbix-deployment.git`
- Change directory: `cd zabbix-deployment`
- Clean up local repo: `rm -Rf .git` 
- Customize values: `env_vars/.env_web` AND `env_vars/.env_db_pgsql`
- Inject secrets: `env_vars/.POSTGRES_USER` AND `env_vars/.POSTGRES_PASSWORD`
- Setup compose: `cp docker-compose.production.yml docker-compose.yml`
- Apply deployment: `docker-compose up -d`

## Deploy local environment

- Clone this repo on docker node: `git clone git@github.com:SindriaInc/zabbix-deployment.git`
- Change directory: `cd zabbix-deployment`
- Clean up local repo: `rm -Rf .git`
- Setup compose: `cp docker-compose.local.yml docker-compose.yml`
- Apply deployment: `docker-compose up -d`

# Tips and Tricks

- Watch logs: `docker-compose logs -f`
- Watch logs of specific service: `docker-compose logs -f <service>`
- Destroy env: `docker-compose down`
- Destroy all: `docker-compose down -v`

N.B. if one or more services doesn't start, run following commands:

1.`docker-compose up -d proxy`

2.`docker-compose up -d web_service`

3.`docker-compose up -d agent`