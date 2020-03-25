#!/bin/bash

# NOTE: there is no way to extract a single file from github using the git interface.
# However, github provides an http endpoint that can do it. We assume curl is available.
tmpfile=/tmp/data-repository-openapi.yaml
curl \
 https://raw.githubusercontent.com/DataBiosphere/jade-data-repo/develop/src/main/resources/data-repository-openapi.yaml \
 -o $tmpfile

diffResult=$(diff ./src/main/resources/data-repository-openapi.yaml $tmpfile)

if [ -z "$diffResult" ]
then
  echo "API is up to date"
  exit 0
else
  echo "$(diffResult)"
  echo "API is NOT up to date"
  exit 1
fi
