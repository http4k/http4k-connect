#!/bin/bash

set -e
set -o errexit
set -o pipefail
set -o nounset

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

LOCAL_VERSION=$(jq -r .connect.version $DIR/version.json)

function maven_publish() {
    local PACKAGE=$1
    local PAYLOAD="{\"username\": \"${SONATYPE_USER}\", \"password\": \"${SONATYPE_KEY}\"}"

    local PUBLISHED=$(
        curl --fail --silent -o /dev/null https://repo.maven.apache.org/maven2/org/http4k/"${PACKAGE}"/"${LOCAL_VERSION}"/"${PACKAGE}"-"${LOCAL_VERSION}".pom
        echo $?
    )

    if [[ $PUBLISHED == "0" ]]; then
        echo "$PACKAGE is already published. Skipping"
    else
        echo "Publishing $PACKAGE $LOCAL_VERSION into Maven central..."
        RESULT=$(curl -s -X POST -u "$BINTRAY_USER:$BINTRAY_KEY" -H "Content-Type: application/json" --data "$PAYLOAD" "https://bintray.com/api/v1/maven_central_sync/http4k/maven/$PACKAGE/versions/$LOCAL_VERSION")

        if [[ ! "${RESULT}" =~ .*Successful.* ]]; then
            echo "Failed: ${RESULT}"
        fi
    fi
}
