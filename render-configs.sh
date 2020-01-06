#!/bin/bash

LOCAL_TOKEN=$(cat ~/.vault-token)
VAULT_TOKEN=${1:-$LOCAL_TOKEN}

# read the client secret from Vault and store in a JSON file under /tmp
docker run --rm -e VAULT_TOKEN=$VAULT_TOKEN broadinstitute/dsde-toolbox:latest \
    vault read -format=json secret/dsde/datarepo/dev/cli-key.json | \
    jq .data > /tmp/jadecli_client_secret.json

docker run --rm -e VAULT_TOKEN=$VAULT_TOKEN broadinstitute/dsde-toolbox:latest \
    vault read -format=json secret/dsde/datarepo/dev/sa-key.json | \
    jq .data | tee /tmp/jade-dev-account.json | \
    jq -r .private_key > /tmp/jade-dev-account.pem

# TODO: write the service account access token to ~/.creds directory
# TODO: write the context.properties file, also under /tmp
