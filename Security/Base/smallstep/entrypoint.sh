#!/bin/bash
set -e

CA_DIR="/lab/step-ca"
CERT_DIR="/lab/certs"
DOMAIN="*.sindria.corp"
PASSWORD="changeit"

mkdir -p "$CA_DIR" "$CERT_DIR"

echo "🔐 Inizializzo la CA..."

# Init CA
step ca init --name "Sindria Internal CA" \
  --dns "ca.internal" \
  --address ":9000" \
  --provisioner acme \
  --provisioner-type ACME \
  --provisioner-password-file <(echo "$PASSWORD") \
  --password-file <(echo "$PASSWORD") \
  --root "$CA_DIR/root_ca.crt" \
  --crt "$CA_DIR/intermediate_ca.crt" \
  --key "$CA_DIR/intermediate_ca_key" \
  --with-ca-url "https://ca.internal"

# Start step-ca in background
echo "🚀 Avvio step-ca in background..."
step-ca "$CA_DIR/config/ca.json" &

CA_PID=$!
sleep 3

echo "📡 Richiedo certificato ACME con lego (dns-01 + Route 53)..."

lego \
  --email you@example.com \
  --dns route53 \
  --domains "$DOMAIN" \
  --server https://ca.internal/acme/acme/directory \
  --path "$CERT_DIR" \
  run

kill $CA_PID

echo "🎉 Certificato creato in $CERT_DIR"
ls -l "$CERT_DIR"

# Sync to S3 if set
if [ -n "$S3_BUCKET" ]; then
  echo "☁️  Eseguo sync di /lab verso $S3_BUCKET ..."
  aws s3 sync /lab "$S3_BUCKET" --sse
  echo "✅ Sync completato"
else
  echo "⚠️  Variabile S3_BUCKET non settata, salto l'upload"
fi