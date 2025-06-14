# Academy V1 Web Portal

This repo is for web portal of xpipe platform.

## Setup Development Environment

- Setup env: `cp .env.local .env`
- Setup docker compose: `cp docker-compose.local.yml docker-compose.yml`
- Start environment: `docker-compose up -d`
- Enter into container: `docker exec -it academy-v1-web-portal bash`
- Install dependencies: `composer install` (after this exit from container)
- Install product: `bash bin/magento_setup.sh Carbon User carbon.user@sindria.org carbon.user admin123`
- Setup upgrade: `php bin/magento setup:upgrade`
- Setup di compile: `php bin/magento setup:di:compile`
- Deploy assets: `php bin/magento setup:static-content:deploy it_IT en_US -f`
- Reindex magento catalog: `php bin/magento indexer:reindex`
- Change deploy mode (optional): `php bin/magento deploy:mode:set developer`
- Flush cache: `php bin/magento cache:flush`
- Rehash default users passwords: `php bin/magento pipe:users:rehash-passwords`
- Watch logs: `docker compose logs -f app`

## Lazy mode

- Run Automatic Cleaner: `bash bin/automatic_cleaner.sh`
- Setup env: `cp .env.local .env`
- Setup docker compose: `cp docker-compose.local.yml docker-compose.yml`
- Start environment: `docker-compose up -d`
- Run Automatic Installer: `bash bin/automatic_installer.sh`
- Watch logs: `docker compose logs -f app`

## Common errors

- Magento permissions: `bash bin/magento_fix_permissions.sh`
- Magento frontend: `bash bin/magento_flush_frontend.sh`
- Magento generated: `bash bin/magento_clean_generated.sh`
- Magento var: `bash bin/magento_clean_var.sh`

If you have problem with db service try comment this line on docker-compose.yml


```yaml
command: [ "--default-authentication-plugin=mysql_native_password" ]
```

INTO

```yaml
#command: [ "--default-authentication-plugin=mysql_native_password" ]
```


## Destroy local env

- Delete product files: `rm src/app/etc/config.php` AND `rm src/app/etc/env.php`
- Destroy containers and volumes: `docker compose down -v` OR `docker-compose down -v`

## Utility commands

- Container status: `docker ps`
- Watch all container logs: `docker compose logs -f`
- Watch specific container logs: `docker compose logs -f <service>`
- Enter into container: `docker exec -it academy-v1-web-portal bash`
- Clean magento cache: `php bin/magento cache:clean`
- Reindex magento catalog: `php bin/magento indexer:reindex`