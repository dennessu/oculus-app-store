#!/usr/bin/env bash

export APPHOST_CLI_OPTS="-DconfigDir=./conf -DactiveEnv=onebox -Dlogback.configurationFile=./conf/logback-apphost.xml"
./bin/apphost-cli &