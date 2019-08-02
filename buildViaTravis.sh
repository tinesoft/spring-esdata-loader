#!/bin/bash
# This script will build the project.

SWITCHES="--info --stacktrace"

if [ "$TRAVIS_PULL_REQUEST" == "false" ]; then
  echo -e "Build Release"
  ./gradlew release gitPublishPush $SWITCHES
else
  echo -e 'Build Pull Request ['$TRAVIS_PULL_REQUEST']'
  ./gradlew build $SWITCHES
fi

EXIT=$?

exit $EXIT

