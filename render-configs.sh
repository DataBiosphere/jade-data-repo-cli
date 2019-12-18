#!/bin/bash

LOCAL_TOKEN=$(cat ~/.vault-token)
VAULT_TOKEN=${1:-$LOCAL_TOKEN}

docker run --rm -e VAULT_TOKEN=$VAULT_TOKEN broadinstitute/dsde-toolbox:latest \
    mkdir -p ~/.jadecli/client | \
    vault read -format=json secret/dsde/datarepo/dev/sa-key.json | \
    jq .data > ~/.jadecli/client/jadecli_client_secret.json
