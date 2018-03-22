#!/bin/bash

case "$1" in
    core)
        ./gradlew :core:core-server:installDist
        $(dirname $0)/core/core-server/build/install/core-server/bin/core-server &
        ;;
    ldap)
        ./gradlew :common:ldap-test-support:installDist
        $(dirname $0)/common/ldap-test-support/build/install/ldap-test-support/bin/ldap-test-support &
        ;;
    web)
        ./gradlew :web:web-server:installDist
        $(dirname $0)/web/web-server/build/install/web-server/bin/web-server &
        ;;
    *)
        echo "Usage: $0 {core|ldap|web}"
        exit 1
esac

