#!/bin/bash

set -e
set -o errexit
set -o pipefail
set -o nounset

./gradlew check jacocoRootReport --build-cache --parallel --info
bash <(curl -s https://codecov.io/bash)
