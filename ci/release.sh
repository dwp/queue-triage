#!/bin/bash

[ "${TRAVIS_PULL_REQUEST}" != "false" ] && echo "Release phase skipped.  Building Pull Request" && exit 0;
[ "${TRAVIS_BRANCH}" != "master" ]      && echo "Release phase skipped.  Not on master" && exit 0;
[ -z "${RELEASE_CREDS_USERNAME}" ]      && echo "Release phase skipped.  Release credentials not present" && exit 0;

# Perform release with relevant credentials, and ignore checking if current branch is ahead of the remote, because travis will checkout in a detached head state.
./gradlew release -i \
    -Prelease.customUsername=${RELEASE_CREDS_USERNAME} \
    -Prelease.customPassword=${RELEASE_CREDS_PASSWORD} \
    -Prelease.disableRemoteCheck;