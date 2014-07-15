#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

set +e
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "CREATE TABLE dummy_test (id bigint, PRIMARY KEY(id));"
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "CREATE TABLE tly (id bigint, PRIMARY KEY(id));"
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "CREATE TABLE msx (id bigint, PRIMARY KEY(id));"
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "CREATE TABLE gyh (id bigint, PRIMARY KEY(id));"
set -e
