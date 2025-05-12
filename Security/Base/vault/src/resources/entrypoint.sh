#!/usr/bin/env bash

sed -i -E "s|@@VAULT_UI@@|${VAULT_UI}|g" /vault/config/config.hcl
sed -i -E "s|@@VAULT_API_ADDRESS@@|${VAULT_API_ADDRESS}|g" /vault/config/config.hcl
sed -i -E "s|@@VAULT_DISABLE_MLOCK@@|${VAULT_DISABLE_MLOCK}|g" /vault/config/config.hcl

sed -i -E "s|@@VAULT_MYSQL_USERNAME@@|${VAULT_MYSQL_USERNAME}|g" /vault/config/config.hcl
sed -i -E "s|@@VAULT_MYSQL_PASSWORD@@|${VAULT_MYSQL_PASSWORD}|g" /vault/config/config.hcl
sed -i -E "s|@@VAULT_MYSQL_DATABASE@@|${VAULT_MYSQL_DATABASE}|g" /vault/config/config.hcl
sed -i -E "s|@@VAULT_MYSQL_ADDRESS@@|${VAULT_MYSQL_ADDRESS}|g" /vault/config/config.hcl
sed -i -E "s|@@VAULT_MYSQL_PLAINTEXT_CONNECTION_ALLOWED@@|${VAULT_MYSQL_PLAINTEXT_CONNECTION_ALLOWED}|g" /vault/config/config.hcl

sed -i -E "s|@@VAULT_TLS_DISABLE@@|${VAULT_TLS_DISABLE}|g" /vault/config/config.hcl

vault server -config /vault/config/config.hcl