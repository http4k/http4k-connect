#!/bin/bash

set -e
set -o errexit
set -o pipefail
set -o nounset

./gradlew check --debug
#./gradlew check --build-cache --parallel
#bash <(curl -s https://codecov.io/bash)
