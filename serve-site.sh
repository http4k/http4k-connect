#!/usr/bin/env bash

set -e
set -o errexit
set -o pipefail
set -o nounset

pip3 install -r requirements.txt

./tools/embed_code.py

cd src
mkdocs serve
