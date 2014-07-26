#!/bin/bash
source "$(git rev-parse --show-toplevel)/scripts/common.sh"; # this comment is needed, see common.sh for detail
set -e

DIR=`git rev-parse --show-toplevel`

cd $DIR
cd apphost/apphost-cli/build/install/apphost-cli/
mkdir -p ./logs

if [[ "$OSTYPE" == "linux-gnu" ]]; then
    LOG_FILE=/var/silkcloud/logs/main.log
else
    LOG_FILE=./logs/main.log
fi
PID_FILE=./logs/apphost.pid

$DIR/shutdown.sh || true
./startup.sh onebox &
echo $! > $PID_FILE

function cleanup {
    $DIR/shutdown.sh
}

trap cleanup EXIT INT TERM

# wait for the log file to be created.
while [[ ! -f $LOG_FILE ]]; do
    sleep 1
done

echo Waiting until application fully start...
tail -f -n0 $LOG_FILE | while read LOGLINE
do
    if [[ "${LOGLINE}" == *"Started Junbo Application Host"* ]]; then
        ps -aef | grep tail | grep $$ | grep -v grep | awk '{print $2}' | xargs kill
    fi
done

trap - EXIT INT TERM
