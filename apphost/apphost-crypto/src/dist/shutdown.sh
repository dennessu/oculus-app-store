#!/usr/bin/env bash
(set -o igncr) 2>/dev/null && set -o igncr; # ignore \r in windows. The comment is needed.
DIR="$( cd "$( dirname "$0" )" && pwd )"

if [[ "$OSTYPE" == "linux-gnu" ]]; then
    PID_FILE=/var/silkcloud/run/apphost-crypto.pid
else
    PID_FILE=$DIR/logs/apphost-crypto.pid
fi

if [[ -f $PID_FILE ]]; then
  kill `cat "$PID_FILE"`
  sleep 1
  kill -9 `cat "$PID_FILE"` >/dev/null 2>&1
  rm $PID_FILE
fi
