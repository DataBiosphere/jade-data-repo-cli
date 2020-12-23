#!/bin/bash
#
# This script has all of the commands you need to get started using Terra Data Repository (TDR).
# Depending on your situation, you probably won't have to use them all. ItThe script is not designed to be run end-to-end.
# It is more of a "choose your own adventure". Although you could comment out the parts you don't need and run it.
#
# You will need to have the 'jq' tool installed. It is used to parse data from the JSON formatted returns from the cli commands.
# You can learn about jq here: https://stedolan.github.io/jq/
#
# You will need to have 'gsutil' installed. It is used to put files into a bucket for TDR to load.

###########
# Functions - some functions to keep things DRY-ish
###########

function gen_to_gs() {
    LOCALFILE=$(mktemp cli.XXXXXX)
    echo "$1" > ${LOCALFILE}
    GSPATH="gs://${BUCKET}/${LOCALFILE}"
    gsutil cp ${LOCALFILE} ${GSPATH}
    rm ${LOCALFILE}
    echo ${GSPATH}
}

###########
# Step 0. Configure your environment
###########

# Setup an alias for the jadecli that points to where you installed it. Installation instructions can be found
### TODO: specify the installation instructions

alias tdr="<PATH TO YOUR JADECLI>"

# Configure your session to point to the correct backend TDR:
# dev       - https://jade.datarepo-dev.broadinstitute.org
# fake prod - https://jade-terra.datarepo-prod.broadinstitute.org
### TODO: find the rest of the backend paths

tdr session set basepath https://jade.datarepo-dev.broadinstitute.org

# Login. For now, the CLI is not vetted by Google, so you have to navigate through the security warning screens
# and accept the scopes. The login command will launch a browser window for you to choose your user.
# See `tdr help auth sa` for how to authorize with a service account.

tdr auth login

###########
# Step 1. Setup billing
###########
#
# Most users do not need to do this. If you have been given the name of a billing profile to use, then
# fill it in here on the line below and go to step 2. Otherwise, fill in the name of the profile you will
# create.

BILLING_PROFILE="<YOUR PROFILE NAME>"

# To create a billing profile, you will need to know a Google billing account that both you and the
# TDR service account have the 'billing.resourceAssociations.create' permissions on. If you are configuring
# the billing account, here are the TDR service accounts for the environments:
# dev       - 
# fake prod -
### TODO: fill in service account names

tdr profile create --name ${BILLING_PROFILE} --account "<YOUR BILLING ACCOUNT>"

###########
# Step 2. Create a dataset paid for with that billing profile
###########
#
# Fill in a name for your dataset. Names must be unique across all datasets in the TDR.
# The dataset contains a simple schema:
#
#  participants donate samples; samples have associated files
#
# We will populate data and files later

DATASET="<YOUR DATASET NAME>"

DATASET_JSON=$(cat <<-END
{
  "name":        "${DATASET}",
  "description": "CLI Getting Started dataset",
  "schema":      {
    "tables":        [
      {
        "name":    "participant",
        "columns": [
          {"name": "id", "datatype": "string"},
          {"name": "age", "datatype": "integer"},
          {"name": "children", "datatype": "string", "array_of": true},
          {"name": "donated", "datatype": "string", "array_of": true}
        ],
        "primaryKey": ["id"]
      },
      {
        "name":    "sample",
        "columns": [
          {"name": "id", "datatype": "string"},
          {"name": "participant_id", "datatype": "string"},
          {"name": "date_collected", "datatype": "date"},
        ],
        "primaryKey": ["id"]
      },
      {
        "name": "file",
        "columns": [
          {"name": "id", "datatype": "fileref"},
          {"name": "sample_id", "datatype": "string"}
        ]
      }
    ],
    "relationships": [
      {
        "name": "participant_samples",
        "from": {"table": "participant", "column": "donated"},
        "to":   {"table": "sample", "column": "id"}
      },
      {
        "name": "sample_participants",
        "from": {"table": "sample", "column": "participant_ids"},
        "to":   {"table": "participant", "column": "id"}
      },
      {
        "name": "participant_children",
        "from": {"table": "participant", "column": "children"},
        "to":   {"table": "participant", "column": "id"}
      },
      {
        "name": "sample_files",
        "from": {"table": "sample", "column": "id"},
        "to":   {"table": "file", "column": "sample_id"}
      }
    ],
    "assets": []
  }
}
END
)

tdr dataset create --name "${DATASET}" --profile "${BILLING_PROFILE}" --input-json "${DATASET_JSON}"

###########
# Step 3. Ingest files into the dataset
###########
#
# TDR loads data from buckets. In order to do the data load, please supply a bucket name. You must have
# read/write access to the bucket. The TDR service account needs read access to the bucket. See Step 1
# for the appropriate service account. Fill your bucket name in here:

BUCKET="<YOUR BUCKET NAME>"

# Generate trivial data files into the bucket. Note: you may have to authenticate with gcloud.

declare -a FILEIDS

for i in `seq 0 5`; do
    GSPATH=$(gen_to_gs "data file $i")
    FILERESULT=$(tdr dataset file load --input-gspath "${GSPATH}" --target-path "/cliSampleData/${LOCALFILE}" --format json "${DATASET}")
    FILEIDS[$i] = jq -r .fileId
done

# We will use the FILEIDS array next when we load table data into the dataset

###########
# Step 4. Load tables into the dataset
###########
#
# To keep this all in one script file, we embed the file data here.
# The load format is JSON with each data row on a single, separate line.

PARTICIPANT_DATA=$(cat <<-END
{"id":"participant_1","age":65,"children":["participant_3","participant_4"],"donated":["sample0"]}
{"id":"participant_2","age":42,"children":["participant_3","participant_5"],"donated":["sample1","sample3"]}
{"id":"participant_3","age":15,"children":[],"donated":["sample2","sample4"]}
{"id":"participant_4","age":27,"children":["participant_5"],"donated":[]}
{"id":"participant_5","age":3,"children":[],"donated":[]}
END
)

SAMPLE_DATA=$(cat <<-END
{"id":"sample0","participant_id":"participant_1","date_collected":"2019-02-27"}
{"id":"sample1","participant_id":"participant_2","date_collected":"2019-02-27"}
{"id":"sample2","participant_id":"participant_3","date_collected":"2020-02-29"}
{"id":"sample3","participant_id":"participant_2","date_collected":"2019-02-28"}
{"id":"sample4","participant_id":"participant_3","date_collected":"2019-03-01"}
END
)

FILE_DATA=$(cat <<-END
{"id":"${FILEIDS[0]","sample_id":"sample0"}
{"id":"${FILEIDS[1]","sample_id":"sample1"}
{"id":"${FILEIDS[2]","sample_id":"sample2"}
{"id":"${FILEIDS[3]","sample_id":"sample3"}
{"id":"${FILEIDS[4]","sample_id":"sample3"}
{"id":"${FILEIDS[5]","sample_id":"sample4"}
END
)

GSPATH=$(gen_to_gs "${PARTICIPANT_DATA}")
tdr dataset table load --table participant --input-gspath "${GSPATH}" "${DATASET}"
GSPATH=$(gen_to_gs "${SAMPLE_DATA}")
tdr dataset table load --table sample --input-gspath "${GSPATH}" "${DATASET}"
GSPATH=$(gen_to_gs "${FILE_DATA}")
tdr dataset table load --table file --input-gspath "${GSPATH}" "${DATASET}"

###########
# Step 5. Make a full snapshot from the dataset
###########
#
# Select a name for your snapshot. Snapshot names must be unique within the TDR

SNAPSHOT="<YOUR SNAPSHOT NAME HERE>"

SNAPSHOT_JSON=$(cat <<-END
{
  "name":"${SNAPSHOT}",
  "description":"CLI getting started snapshot ",
  "contents":[
    {
      "datasetName": "${DATASET}",
      "mode": "byFullView"
    }
  ]
}
END
)

tdr snapshot create --name "${SNAPSHOT}" --profile "${BILLING_PROFILE}" --input-json "${SNAPSHOT_JSON}"
