#!/bin/bash

set -e
set -o errexit
set -o pipefail
set -o nounset

NEW_VERSION=$1

echo "Attempting to release $NEW_VERSION"

./gradlew -PreleaseVersion="$NEW_VERSION" clean test \
    http4k-connect-bom:bintrayUpload \
    http4k-connect-example:bintrayUpload \
    http4k-connect-example-fake:bintrayUpload \
    http4k-connect-google-analytics:bintrayUpload \
    http4k-connect-google-analytics-fake:bintrayUpload
