#!/bin/sh
source "$(git rev-parse --show-toplevel)/scripts/common.sh"; # this comment is needed, see common.sh for detail

pushd liquibase
./dropdb.sh
./createdb.sh
./updatedb.sh
popd

./build clean build install
