#!/bin/bash

#
# Initialize the custom data directory layout
#
source /Users/lucapitzoi/XPipe/Security/Base/ossec/src/root_fs/data_dirs.env

cd /var/ossec
for ossecdir in "${DATA_DIRS[@]}"; do
  mv ${ossecdir} ${ossecdir}-template
  ln -s $(realpath --relative-to=$(dirname ${ossecdir}) data)/${ossecdir} ${ossecdir}
done
