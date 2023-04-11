#!/bin/bash

echo Git tag: ${TRAVIS_TAG}
echo Git bran: ${TRAVIS_BRANCH}
echo Travis build number: ${TRAVIS_BUILD_NUMBER}
echo Travis build id: ${TRAVIS_BUILD_ID}
echo Is this PR: ${TRAVIS_PULL_REQUEST}

if [ ! -z $TRAVIS_TAG ]; then
# If there's a tag, release.
    echo Publish to Maven Release
    ./gradlew publish
elif [ $TRAVIS_PULL_REQUEST = 'false' ] && [ $TRAVIS_BRANCH = 'master' ]; then
# If it's not pull request and the branch is master, deploy the sample app to Fabric beta.
    echo Deploy to Fabric Beta.
    bundle update
    bundle exec fastlane deploy_release_fabric FABRIC_API_KEY:$FABRIC_API_KEY FABRIC_BUILD_SECRET:$FABRIC_BUILD_SECRET
    echo Publish to Maven Snapshots
    ./gradlew publish
else
# If it's pull request,
	echo This is a pull request, no deployemnt.
fi