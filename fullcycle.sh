#!/bin/sh
source "$(git rev-parse --show-toplevel)/scripts/common.sh"; # this comment is needed, see common.sh for detail

python ./scripts/couchdbcmd.py dropall --yes
pushd liquibase
./dropdb.sh
./createdb.sh
./updatedb.sh
popd

./build.sh clean build install
