#!/bin/bash

LOCAL_TOKEN=$(cat ~/.vault-token)
VAULT_TOKEN=${1:-$LOCAL_TOKEN}

# uncomment this once we have a solution for Travis running integration tests that require authentication
#docker run --rm -e VAULT_TOKEN=$VAULT_TOKEN broadinstitute/dsde-toolbox:latest \
#    vault read -format=json secret/dsde/datarepo/dev/cli-key.json | \
#    jq .data > /tmp/jadecli_client_secret.json
