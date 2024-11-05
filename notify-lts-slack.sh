#!/bin/bash

set -e
set -o errexit
set -o pipefail
set -o nounset

if [[ $1 == 4* ]]; then
  MESSAGE="Released http4k-connect LTS version $1 (See <https://github.com/http4k/http4k-connect-lts-v4/blob/master/CHANGELOG.md|Changelog> for details)."
  curl -X POST -H 'Content-type: application/json' --data "{\"text\":\"$MESSAGE\"}" $LTS_SLACK_WEBHOOK
else
  MESSAGE="Released http4k-connect OSS version $1 (See <https://www.http4k.org/ecosystem/http4k-connect/changelog/|Changelog> for details)."
  curl -X POST -H 'Content-type: application/json' --data "{\"text\":\"$MESSAGE\"}" $LTS_SLACK_WEBHOOK
fi
