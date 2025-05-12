#!/usr/bin/env bash


curl -X POST -is -u ${IAC_GIT_USERNAME}:${IAC_GIT_PASSWORD} \
  -H 'Content-Type: application/json' \
 https://api.bitbucket.org/2.0/repositories/Cyberefund/customer-01/pipelines/ \
  -d '
  {
    "target": {
      "ref_type": "branch",
      "type": "pipeline_ref_target",
      "ref_name": "master"
    },
    "variables": [
    {
      "key": "CYR_COMPANY_NAME",
      "value": "${CYR_COMPANY_NAME}",
      "secured": false
    },
    {
      "key": "CYR_PACKAGE",
      "value": "${CYR_PACKAGE}",
      "secured": false
    },
    {
      "key": "CYR_PIVA",
      "value": "${CYR_PIVA}",
      "secured": false
    },
    {
      "key": "CYR_WEBURL",
      "value": "${CYR_WEBURL}",
      "secured": false
    },
    {
      "key": "CYR_REFERENTE",
      "value": "${CYR_REFERENTE}",
      "secured": false
    },
    {
      "key": "CYR_REFERENTEMAIL",
      "value": "${CYR_REFERENTEMAIL}",
      "secured": false
    },
    {
      "key": "CYR_REFERENTEPHONE",
      "value": "${CYR_REFERENTEPHONE}",
      "secured": false
    }
  ]
  }'