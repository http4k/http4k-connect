#!/bin/bash

set -e
set -o errexit
set -o pipefail
set -o nounset

MESSAGE="Released http4k-connect version $1 (See <https://github.com/http4k/http4k-connect/blob/master/CHANGELOG.md#changelog|Changelog> for details)."
curl -X POST -H 'Content-type: application/json' --data "{'text':'$MESSAGE'}" $SLACK_WEBHOOK
