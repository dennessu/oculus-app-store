#!/bin/bash
source "$(git rev-parse --show-toplevel)/scripts/common.sh"; # this comment is needed, see common.sh for detail
set -e

DIR=`git rev-parse --show-toplevel`
cd $DIR

PID_FILE=.tokyo-proxy.pid

# wait for 3 seconds to gracefully shutdown
TIMEOUT=3

# if force shutdown, change TIMEOUT to 1 second
if [[ "$1" == "-f" ]]; then
  TIMEOUT=1
fi

if [[ -f $PID_FILE ]]; then
  echo Stopping proxy...
  APPHOST_PID=`cat "$PID_FILE"`
  kill $APPHOST_PID

  time_waited=0
  while [ $time_waited -lt $TIMEOUT ]; do
    if ! kill -0 $APPHOST_PID > /dev/null 2>&1; then
      echo Proxy stopped.
      break
    fi

    sleep 1
    time_waited=$[$time_waited+1]
    echo Waiting for PID $APPHOST_PID to stop... ${time_waited}
  done

  if kill -0 $APPHOST_PID > /dev/null 2>&1; then
    echo The process is still running, force killing apphost process with PID $APPHOST_PID.
    kill -9 $APPHOST_PID
  fi
  rm $PID_FILE
fi

