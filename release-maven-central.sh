#!/bin/bash

source ./release-functions.sh

./gradlew -q listProjects | while read -r line ; do
    maven_publish "$line"
done
