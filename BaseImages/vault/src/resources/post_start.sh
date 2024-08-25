#!/usr/bin/env bash

if [[ -z "$1" ]]; then
    echo "Provide vault address as first argument (eg. https://sindria-vault-xpipe.sindria.org)"
    exit 1
fi

VAULT_ADDRESS=$1
VAULT_KEY_SHARES=5
VAULT_KEY_THRESHOLD=2

vault operator init -address=${VAULT_ADDRESS} -key-shares=${VAULT_KEY_SHARES} -key-threshold=${VAULT_KEY_THRESHOLD} > /vault/data/keys.txt