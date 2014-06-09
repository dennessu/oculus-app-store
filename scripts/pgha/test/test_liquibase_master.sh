#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

set +e
$PGBIN_PATH/dropdb repl_test -h $MASTER_HOST -p $MASTER_DB_PORT
$PGBIN_PATH/createdb repl_test -h $MASTER_HOST -p $MASTER_DB_PORT
$PGBIN_PATH/psql repl_test -h $MASTER_HOST -p $MASTER_DB_PORT -c "CREATE TABLE student (id bigint);"
$PGBIN_PATH/psql repl_test -h $MASTER_HOST -p $MASTER_DB_PORT -c "CREATE TABLE teacher (id bigint);"
set -e
