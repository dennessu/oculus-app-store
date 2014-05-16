#!/usr/bin/env bash
if [ ! -d "./logs" ]; then
    mkdir logs
fi

export APPHOST_CLI_OPTS="-DconfigDir=./conf -DactiveEnv=onebox"
./bin/apphost-cli &
echo $! > ./logs/app.pid
