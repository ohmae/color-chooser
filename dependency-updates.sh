#!/bin/sh
if [ -z "$1" ]; then
    ./gradlew dependencyUpdates --no-configuration-cache --no-parallel
else
    ./gradlew :"${1}":dependencyUpdates --no-configuration-cache --no-parallel
fi
