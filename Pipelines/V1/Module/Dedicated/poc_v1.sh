# === SPOSTA LA ISSUE IN UNA COLONNA (STATUS FIELD) ===
DESIRED_STATUS="To do"

echo "🔍 Cerco il campo 'Status' e l’opzione '$DESIRED_STATUS'..."

GRAPHQL_FIELDS_QUERY='
{
  "query": "query { node(id: \"'"$PROJECT_ID"'\") { ... on ProjectV2 { fields(first: 20) { nodes { ... on ProjectV2SingleSelectField { id name options { id name } } } } } } }"
}
'

FIELDS_RESPONSE=$(curl -s -H "Authorization: Bearer $GITHUB_TOKEN" \
  -H "Content-Type: application/json" \
  -X POST https://api.github.com/graphql \
  -d "$GRAPHQL_FIELDS_QUERY")

FIELD_ID=$(echo "$FIELDS_RESPONSE" | jq -r '.data.node.fields.nodes[] | select(.name=="Status") | .id')
OPTION_ID=$(echo "$FIELDS_RESPONSE" | jq -r '.data.node.fields.nodes[] | select(.name=="Status") | .options[] | select(.name=="'"$DESIRED_STATUS"'") | .id')

if [ -z "$FIELD_ID" ] || [ -z "$OPTION_ID" ]; then
  echo "❌ Errore: campo 'Status' o opzione '$DESIRED_STATUS' non trovati."
  exit 1
fi

echo "🔄 Sposto la issue nello stato '$DESIRED_STATUS'..."

GRAPHQL_UPDATE_FIELD='
{
  "query": "mutation { updateProjectV2ItemFieldValue(input: { projectId: \"'"$PROJECT_ID"'\", itemId: \"'"$ITEM_ID"'\", fieldId: \"'"$FIELD_ID"'\", value: { singleSelectOptionId: \"'"$OPTION_ID"'\" } }) { projectV2Item { id } } }"
}
'

UPDATE_RESPONSE=$(curl -s -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Content-Type: application/json" \
     -X POST https://api.github.com/graphql \
     -d "$GRAPHQL_UPDATE_FIELD")

if echo "$UPDATE_RESPONSE" | grep -q '"projectV2Item"'; then
  echo "✅ La issue è stata spostata in '$DESIRED_STATUS'."
else
  echo "⚠️ Qualcosa è andato storto:"
  echo "$UPDATE_RESPONSE"
fi
