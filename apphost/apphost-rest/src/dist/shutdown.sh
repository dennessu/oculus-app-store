#!/usr/bin/env bash

PID_FILE=./logs/apphost-rest.pid
if [[ -d /var/silkcloud ]]; then
    mkdir -p /var/silkcloud/run
    PID_FILE=/var/silkcloud/run/apphost-rest.pid
fi

if [[ -f $PID_FILE ]]; then
  kill -9 `cat "$PID_FILE"` >/dev/null 2>&1
fi