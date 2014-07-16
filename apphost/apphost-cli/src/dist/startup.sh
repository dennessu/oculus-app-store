#!/usr/bin/env bash
(set -o igncr) 2>/dev/null && set -o igncr; # ignore \r in windows. The comment is needed.
DIR="$( cd "$( dirname "$0" )" && pwd )"

if [[ "$OSTYPE" == "linux-gnu" ]]; then
    mkdir -p /var/silkcloud/logs
    mkdir -p /var/silkcloud/run
    PID_FILE=/var/silkcloud/run/apphost-cli.pid
else
    mkdir -p logs
    PID_FILE=./logs/apphost-cli.pid
fi

export APPHOST_CLI_OPTS=""

# check environment
silkcloudenv=$1
if [[ "$silkcloudenv" == "" ]]; then
    if ! grep '^environment=[a-zA-Z0-9_]\+' /etc/silkcloud/configuration.properties; then
        echo "ERROR: environment not set!"
        exit 1
    fi
else
    export APPHOST_CLI_OPTS="$APPHOST_CLI_OPTS -Denvironment=$silkcloudenv"
fi

./bin/apphost-cli &
echo $! > $PID_FILE
