#!/bin/bash

set -e
set -o errexit
set -o pipefail
set -o nounset

NEW_VERSION=$1

git tag -a "$NEW_VERSION" -m "http4k-connect version $NEW_VERSION"
git push origin "$NEW_VERSION"
