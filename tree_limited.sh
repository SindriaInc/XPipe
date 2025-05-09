#!/bin/sh

MAX_CHILDREN=${1:-3}
MAX_DEPTH=${2:-5}
ONLY_DIRS=${3:-false}
OUTPUT_FILE="tree_limited.txt"

# Elementi da escludere
EXCLUDES=".git .gitignore node_modules .DS_Store venv __pycache__ .idea tmp readme.md tree_limited.sh tree_limited.txt"

# Verifica se un nome deve essere escluso
should_exclude() {
  for pattern in $EXCLUDES; do
    [ "$1" = "$pattern" ] && return 0
  done
  return 1
}

# Funzione principale ricorsiva
print_tree() {
  path="$1"
  prefix="$2"
  depth="$3"

  if [ "$depth" -ge "$MAX_DEPTH" ]; then
    return
  fi

  count=0

  find "$path" -mindepth 1 -maxdepth 1 -print | LC_ALL=C sort | while IFS= read -r child; do
    name=$(basename "$child")

    if should_exclude "$name"; then
      continue
    fi

    if [ "$ONLY_DIRS" = "true" ] && [ ! -d "$child" ]; then
      continue
    fi

    if [ "$count" -ge "$MAX_CHILDREN" ]; then
      break
    fi

    echo "${prefix}├── $name" >> "$OUTPUT_FILE"

    if [ -d "$child" ]; then
      # Chiamata ricorsiva in subshell per evitare perdita dello scope del while
      (
        print_tree "$child" "${prefix}│   " $((depth + 1))
      )
    fi

    count=$((count + 1))
  done
}

# Output iniziale
echo "# Tree generated on $(date) — max $MAX_CHILDREN children per dir, depth $MAX_DEPTH, only_dirs=$ONLY_DIRS" > "$OUTPUT_FILE"
echo "." >> "$OUTPUT_FILE"
print_tree "." "" 0

echo "✅ Tree salvato in $OUTPUT_FILE (escludendo: $EXCLUDES)"

