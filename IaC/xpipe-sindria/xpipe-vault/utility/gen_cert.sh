#!/usr/bin/env bash

VAULT_SELFSIGNED_CERT_SUBJ="/C=IT/ST=Italy/L=MI/O=MI/OU=MI/CN=*.sindria.org emailAddress=info@sindria.org"

openssl req -x509 -nodes -newkey rsa:2048 -keyout /vault/data/privkey.pem -out /vault/data/fullchain.pem -days 3650 -subj "${VAULT_SELFSIGNED_CERT_SUBJ}"