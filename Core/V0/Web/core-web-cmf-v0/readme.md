# XPipe CMF

This repo contain a core content management framework implementation of xpipe platform

## Setup Development Environment

- Setup env: `cp .env.local .env` (Edit your BASE_PATH env with your user and other stuff)
- Setup docker compose: `cp docker-compose.local.yml docker-compose.yml`
- Start environment: `docker-compose up -d`
- Setup app env: `cp src/.env.local src/.env`
- Exec into container: `docker exec -it xpipe-cmf bash`
- Run composer install: `composer install`
- Run artisan migrate:fresh command: `php artisan migrate:fresh --seed`
- Install node_modules: `npm install`
- Compile frontend assets: `npm run dev`

## Rollback Fresh Install

- Move into repository root
- Destroy environment: `docker-compose down -v`
- Start environment: `docker-compose up -d`
- Exec into container: `docker exec -it xpipe-cmf bash`
- Run artisan migrate:fresh command: `php artisan migrate:fresh --seed`

## Upgrade Development Environment

- Move into repository root
- Destroy environment: `docker-compose down -v`
- Setup env: `cp .env.local .env` (Edit your BASE_PATH env)
- Setup docker compose: `cp docker-compose.local.yml docker-compose.yml`
- Start environment: `docker-compose up -d`
- Setup app env: `cp src/.env.local src/.env`
- Exec into container: `docker exec -it xpipe-cmf bash`
- Run artisan migrate:fresh command: `php artisan migrate:fresh --seed`
- Install node_modules: `npm install`
- Compile frontend assets: `npm run dev`

N.B. if'you don't understand something check out [devops guidelines]() in the wiki.

## Tips and Tricks

- Watch logs: `docker-compose logs -f`
- Watch logs of specific service: `docker-compose logs -f <service>` eg. `docker-compose logs -f app`
- Stop environment: `docker-compose stop`
- Stop specific container: `docker stop <container-name>` eg. `docker stop xpipe-cmf`
- Destroy specific container: `docker rm <container-name>` eg. `docker rm xpipe-cmf`
- Destroy environment: `docker-compose down`
- Destroy environment with volumes: `docker-compose down -v`
- Enter into app container: `docker exec -it xpipe-cmf bash`
- Enter into db container: `docker exec -it xpipe-cmf-db bash`
- Connect to db: `mysql -u root -p` then enter password: `secret`
- Restore/Import dump to db schema: `mysql -u root -p app < file_name_with_data.sql` then enter password: `secret`
- Exec db dump with data: `mysqldump -h db_host -u db_user -p db_schema > file_name_with_data.sql`
- Exec db dump without data: `mysqldump -d -h db_host -u db_user -p db_schema > file_name_without_data.sql`

## Release

`bash bin/release.sh <version> <message>` eg. `bash bin/release.sh 1.0.0-dev "Release 1.0.0-dev"` 

*WARNING:* _Remember to run this command only on the root of the repository!_

## Environments

- XPipe Local: [https://local-demo-xpipe.sindria.org](https://local-demo-xpipe.sindria.org)
- XPipe Demo: [https://demo-xpipe.sindria.org](https://demo-xpipe.sindria.org)

## Users

- Role: Administrator - Username: `admin` - Password: `admin`
- Role: Operator - Username: `operator` - Password: `operator`