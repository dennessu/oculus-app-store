#!/usr/bin/env bash

export IDENTITY_BUNDLE_OPTS="-DconfigDir=./conf -DactiveEnv=onebox"
./bin/identity-bundle > ./logs/out.log 2>&1 &