#!/usr/bin/env bash

export CATALOG_BUNDLE_OPTS="-DconfigDir=./conf -DactiveEnv=onebox"
./bin/catalog-bundle > ./logs/out.log 2>&1 &