#!/usr/bin/env bash
if [ ! -d "./logs" ]; then
    mkdir logs
fi

export APPHOST_CLI_OPTS="-DconfigDir=./conf"
./bin/apphost-cli &
echo $! > ./logs/app.pid
