#!/usr/bin/env bash
(set -o igncr) 2>/dev/null && set -o igncr; # ignore \r in windows. The comment is needed.
DIR="$( cd "$( dirname "$0" )" && pwd )"

export APPHOST_DATALOADER_OPTS=""

# check environment
if ! grep '^environment=[a-zA-Z0-9_]\+' /etc/silkcloud/configuration.properties > /dev/null 2>&1; then
    echo "Set default environment to onebox"
    export APPHOST_DATALOADER_OPTS="$APPHOST_DATALOADER_OPTS -Denvironment=onebox"
fi

exec $DIR/bin/apphost-dataloader "$@"
