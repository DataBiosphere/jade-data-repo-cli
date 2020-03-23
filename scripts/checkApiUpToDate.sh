#!/bin/bash

# NOTE: there is no way to extract a single file from github using the git interface.
# However, github provides a subversion interface and that does let you grab a file.
# So we first test to see if svn is available. If not, we fail. If so, we use it to grab
# the file and do the compare.
svnimage=$(which svn)
if [ -z "$svnimage" ]; then
  echo "No svn so cannot retrieve the api yaml file"
  exit 2
fi

tmpfile=/tmp/data-repository-openapi.yaml
svn export --force --quiet \
  https://github.com/DataBiosphere/jade-data-repo/trunk/src/main/resources/data-repository-openapi.yaml \
  $tmpfile

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
