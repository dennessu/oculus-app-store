#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

set +e
$PGBIN_PATH/dropdb repl_test -h $REPLICA_HOST -p $REPLICA_DB_PORT
$PGBIN_PATH/createdb repl_test -h $REPLICA_HOST -p $REPLICA_DB_PORT
$PGBIN_PATH/psql repl_test -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "CREATE TABLE student (id bigint, PRIMARY KEY(id));"
$PGBIN_PATH/psql repl_test -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "CREATE TABLE teacher (id bigint, PRIMARY KEY(id));"
set -e
