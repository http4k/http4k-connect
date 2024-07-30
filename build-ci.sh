#!/bin/bash

set -e
set -o errexit
set -o pipefail
set -o nounset

#./gradlew check --info
./gradlew check --build-cache --parallel
bash <(curl -s https://codecov.io/bash)
