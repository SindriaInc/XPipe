#!/bin/bash

# === CONFIGURAZIONE ===
GITHUB_TOKEN="ghp_ilTuoTokenQui"
ORG_NAME="nome-org-o-utente"
REPO_NAME="nome-repo"
PROJECT_NUMBER=1

ISSUE_TITLE="Bug: login fallisce su Safari"
ISSUE_BODY="Quando l’utente prova a loggarsi da Safari, la risposta è 403."

# === CREAZIONE ISSUE (REST) ===
echo "🔧 Creo una nuova issue..."
ISSUE_RESPONSE=$(curl -s -X POST \
  -H "Authorization: Bearer $GITHUB_TOKEN" \
  -H "Accept: application/vnd.github+json" \
  https://api.github.com/repos/$ORG_NAME/$REPO_NAME/issues \
  -d '{
    "title": "'"$ISSUE_TITLE"'",
    "body": "'"$ISSUE_BODY"'"
  }')

ISSUE_NODE_ID=$(echo "$ISSUE_RESPONSE" | grep '"node_id"' | cut -d '"' -f 4)
ISSUE_NUMBER=$(echo "$ISSUE_RESPONSE" | grep '"number"' | head -n 1 | grep -o '[0-9]\+')

if [ -z "$ISSUE_NODE_ID" ]; then
  echo "❌ Errore nella creazione della issue"
  echo "$ISSUE_RESPONSE"
  exit 1
fi

echo "✅ Issue creata (#$ISSUE_NUMBER) con node_id: $ISSUE_NODE_ID"

# === RECUPERO PROJECT ID (GraphQL) ===
echo "🔍 Recupero ID del progetto..."

GRAPHQL_PROJECT_QUERY='
{
  "query": "query { organization(login: \"'"$ORG_NAME"'\") { projectV2(number: '"$PROJECT_NUMBER"') { id title } } }"
}
'

PROJECT_RESPONSE=$(curl -s -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Content-Type: application/json" \
     -X POST https://api.github.com/graphql \
     -d "$GRAPHQL_PROJECT_QUERY")

PROJECT_ID=$(echo "$PROJECT_RESPONSE" | grep '"id"' | head -n 1 | cut -d '"' -f 4)

if [ -z "$PROJECT_ID" ]; then
  echo "❌ Errore nel recupero dell'ID progetto"
  echo "$PROJECT_RESPONSE"
  exit 1
fi

echo "✅ Progetto trovato con ID: $PROJECT_ID"

# === AGGIUNTA DELLA ISSUE AL PROGETTO (GraphQL Mutation) ===
echo "➕ Aggiungo la issue alla board..."

GRAPHQL_ADD_ISSUE='
{
  "query": "mutation { addProjectV2ItemById(input: { projectId: \"'"$PROJECT_ID"'\", contentId: \"'"$ISSUE_NODE_ID"'\" }) { item { id } } }"
}
'

ADD_RESPONSE=$(curl -s -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Content-Type: application/json" \
     -X POST https://api.github.com/graphql \
     -d "$GRAPHQL_ADD_ISSUE")

ITEM_ID=$(echo "$ADD_RESPONSE" | grep '"id"' | cut -d '"' -f 4)

if [ -z "$ITEM_ID" ]; then
  echo "❌ Errore nell'aggiunta della issue alla board"
  echo "$ADD_RESPONSE"
  exit 1
fi

echo "🎉 Issue #$ISSUE_NUMBER aggiunta con successo al progetto Kanban!"
