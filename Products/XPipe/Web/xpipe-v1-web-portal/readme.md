# XPipe V1 Web Portal

This repo is for web portal of xpipe platform.

## Setup Development Environment

- Setup env: `cp .env.local .env`
- Setup docker compose: `cp docker-compose.local.yml docker-compose.yml`
- Setup auth.json: `cp assets/auth.json src/auth.json`
- Start environment: `docker-compose up -d`
- Enter into container: `docker exec -it xpipe-ecommerce bash`
- De-escalate privileges: `su sindria`
- Install dependencies: `composer install` (after this exit twice from container)
- Install product: `bash bin/magento_setup.sh Mario Rossi mario.rossi@sindria.org mario.rossi admin123`
- Deploy assets: `php bin/magento setup:static-content:deploy it_IT en_US -f`

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
- Enter into container: `docker exec -it xpipe-ecommerce bash`
- Login as sindria user: `su sindria`
- Watch nginx logs: `tail -f /var/log/nginx/error.log`
- Watch magento logs: `tail -f var/log/debug.log`
- Clean magento cache: `php bin/magento cache:clean`
- Reindex magento catalog: `php bin/magento indexer:reindex`

NB: *lanciare sempre i comandi magento e composer come utente sindria*

###