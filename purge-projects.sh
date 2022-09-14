#!/bin/bash

VERSION=$1

./gradlew listProjects -q 2> projects.txt

for ARTIFACT in `cat projects.txt`
do
    URL=https://repo.maven.apache.org/maven2/org/http4k/$ARTIFACT/$VERSION/$ARTIFACT-$VERSION.pom
    curl -X PURGE $URL > /dev/null 2>&1

    echo Purging $ARTIFACT $VERSION `curl -sq --head $URL | head -1`
done

rm projects.txt
