#!/bin/bash

NAMESPACE="ingress-nginx"
LABEL="app.kubernetes.io/name=ingress-nginx"
SNIPPET='location = /xmlrpc.php'

echo "🔍 Controllo snippet \"$SNIPPET\" nei pod del namespace \"$NAMESPACE\"..."

pods=$(kubectl get pods -n "$NAMESPACE" -l "$LABEL" -o jsonpath="{.items[*].metadata.name}")

for pod in $pods; do
  echo -n "📦 Pod: $pod ... "
  if kubectl -n "$NAMESPACE" exec "$pod" -- grep -q "$SNIPPET" /etc/nginx/nginx.conf; then
    echo "✅ snippet presente"
  else
    echo "❌ snippet NON trovato"
  fi
done
