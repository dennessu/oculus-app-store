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

# wait until 8081 port is open
retrycount="0"
while [ $retrycount -lt 150 ]; do
  if (: < /dev/tcp/127.0.0.1/8081) 2>/dev/null
  then
    echo "apphost is ready to serve, exit this shell script"
    trap - EXIT INT TERM
    exit 0
  else
    echo "port 8081 still not ready yet, waiting..."
    sleep 2
    retrycount=$[$retrycount+1]
  fi
done

echo "## Apphost is still not ready after 5 minutes, there might be something wrong."
echo "##   Exit here, apphost log can be found here: $LOGFILE"
trap - EXIT INT TERM
exit 2
