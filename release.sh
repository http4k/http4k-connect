#!/bin/bash

set -e
set -o errexit
set -o pipefail
set -o nounset

NEW_VERSION=$1

git stash

OLD_VERSION=$(cat version.json | tools/jq -r .connect.version)

sed -i '' s/"$OLD_VERSION"/"$NEW_VERSION"/g README.md
sed -i '' s/"$OLD_VERSION"/"$NEW_VERSION"/g version.json

git add README.md

git commit -am"Release $NEW_VERSION"

git push origin

git push

git stash apply
