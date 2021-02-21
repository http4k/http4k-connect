#!/bin/bash
set -e

NEW_VERSION=$1

cat gradle.properties | grep -v "http4k_version" > out.txt
echo "http4k_version=$NEW_VERSION" >> out.txt
mv out.txt gradle.properties
