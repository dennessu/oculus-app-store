#!/usr/bin/env bash
(set -o igncr) 2>/dev/null && set -o igncr; # ignore \r in windows. The comment is needed.
DIR="$( cd "$( dirname "$0" )" && pwd )"

if [[ "$OSTYPE" == "linux-gnu" ]]; then
    mkdir -p /var/silkcloud/logs
    mkdir -p /var/silkcloud/run
    LOG_FILE=/var/silkcloud/logs/main.log
    PID_FILE=/var/silkcloud/run/apphost-crypto.pid
else
    mkdir -p $DIR/logs
    LOG_FILE=$DIR/logs/main.log
    PID_FILE=$DIR/logs/apphost-crypto.pid
fi

export APPHOST_CRYPTO_OPTS=""

# check environment
silkcloudenv=$1
if [[ "$silkcloudenv" == "" ]]; then
    if ! grep '^environment=[a-zA-Z0-9_]\+' /etc/silkcloud/configuration.properties; then
        echo "ERROR: environment not set!"
        exit 1
    fi
else
    export APPHOST_CRYPTO_OPTS="$APPHOST_CRYPTO_OPTS -Denvironment=$silkcloudenv"
fi

$DIR/bin/apphost-crypto &
echo $! > $PID_FILE

function cleanup {
    $DIR/shutdown.sh
}

trap cleanup EXIT INT TERM

# sleep 1 seconds to ensure main.log is created.
sleep 1

tail -f -n0 $LOG_FILE | while read LOGLINE
do
    if [[ "${LOGLINE}" == *"Started Junbo Application Host"* ]]; then
        ps -aef | grep tail | grep $$ | grep -v grep | awk '{print $2}' | xargs kill
    fi
done

trap - EXIT INT TERM
