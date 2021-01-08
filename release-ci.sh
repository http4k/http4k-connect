#!/bin/bash

#set -e
#set -o errexit
#set -o pipefail
#set -o nounset

NEW_VERSION=$1

echo "Attempting to release $NEW_VERSION"

./gradlew -PreleaseVersion="$NEW_VERSION" test assemble

for i in $(./listProjects.sh); do
    ./gradlew --stacktrace -PreleaseVersion="$NEW_VERSION" :$i:bintrayUpload
done
