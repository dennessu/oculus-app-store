#!/usr/bin/env bash

export COMMERCE_BUNDLE_OPTS="-DconfigDir=./conf -DactiveEnv=onebox"
./bin/commerce-bundle > ./logs/out.log 2>&1 &