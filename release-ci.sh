#!/bin/bash

set -e
set -o errexit
set -o pipefail
set -o nounset

NEW_VERSION=$1

echo "Attempting to release $NEW_VERSION"

./gradlew -PreleaseVersion="$NEW_VERSION" clean test :bintrayUpload
