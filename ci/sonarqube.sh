#!/bin/bash

if [ "${TRAVIS_PULL_REQUEST}" = "false" ]; then
    ./gradlew sonarqube \
        -Dsonar.organization=dwp-queue-triage \
        -Dsonar.host.url=https://sonarcloud.io \
        -Dsonar.login=$SONARQUBE_TOKEN
fi