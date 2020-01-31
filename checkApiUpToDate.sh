#!/bin/bash

diffResult=$(diff ./src/main/resources/data-repository-openapi.yaml \
./jade-data-repo/src/main/resources/data-repository-openapi.yaml)

if [ -z "$diffResult" ]
then
  echo "API is up to date"
  exit 0
else
  echo "$(diffResult)"
  echo "API is NOT up to date"
  exit 1
fi
