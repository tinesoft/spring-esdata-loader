
#!/bin/bash
# This script will build the project.

SWITCHES="--info --stacktrace"

if [ "$TRAVIS_BRANCH" == "master" ]; then
  echo -e 'Build Release'
  ./gradlew publishPackageToBintray gitPublishPush semanticReleasePublish  $SWITCHES
elif [ "$TRAVIS_BRANCH" == "develop" ] && [ "$TRAVIS_PULL_REQUEST" == "false" ]; then
  echo -e 'Build Snapshot'
  ./gradlew artifactoryPublish $SWITCHES
else
    echo -e 'Build Branch ['$TRAVIS_BRANCH'] PR ['$TRAVIS_PULL_REQUEST']'
  ./gradlew build $SWITCHES
fi

EXIT=$?

exit $EXIT
