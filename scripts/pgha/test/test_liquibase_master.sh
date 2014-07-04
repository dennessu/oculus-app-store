#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

set +e
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "CREATE TABLE student (id bigint, PRIMARY KEY(id));"
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "CREATE TABLE teacher (id bigint, PRIMARY KEY(id));"
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "CREATE TABLE perf (id bigint, PRIMARY KEY(id));"
set -e
