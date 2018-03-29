#!/bin/bash

# Perform release with relevant credentials, and ignore checking if current branch is ahead of the remote, because travis will checkout in a detached head state.
if [ ${TRAVIS_PULL_REQUEST} = "false" ] && [ "$TRAVIS_BRANCH" = "master" ]; then
    ./gradlew release -i \
        -Prelease.customUsername=${RELEASE_CREDS_USERNAME} \
        -Prelease.customPassword=${RELEASE_CREDS_PASSWORD} \
        -Prelease.disableRemoteCheck;
fi;