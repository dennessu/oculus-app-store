#!/usr/bin/env bash

if [[ "$OSTYPE" == "linux-gnu" ]]; then
    PID_FILE=/var/silkcloud/run/apphost-identity.pid
else
    PID_FILE=./logs/apphost-identity.pid
fi

if [[ -f $PID_FILE ]]; then
  kill -9 `cat "$PID_FILE"` >/dev/null 2>&1
fi
