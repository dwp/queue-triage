#!/bin/bash

case "$1" in
    core)
        buck build //core/server:start-script
        $(dirname $0)/buck-out/gen/core/server/start-script/start-core.sh &
        ;;
    ldap)
        buck build //common/ldap-test-support:start-script
        $(dirname $0)/buck-out/gen/common/ldap-test-support/start-script/start-ldap.sh &
        ;;
    web)
        buck build //web/server:start-script
        $(dirname $0)/buck-out/gen/web/server/start-script/start-web.sh &
        ;;
    *)
        echo "Usage: $0 {core|ldap|web}"
        exit 1
esac

