#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

set +e
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "CREATE TABLE public.dummy_test (id bigint, PRIMARY KEY(id));"
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "CREATE TABLE public.tly (id bigint, PRIMARY KEY(id));"
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "CREATE TABLE public.msx (id bigint, PRIMARY KEY(id));"
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "CREATE TABLE public.gyh (id bigint, PRIMARY KEY(id));"
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "CREATE TABLE public.databasechangelog (id bigint, PRIMARY KEY(id));"
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "CREATE TABLE public.databasechangeloglock (id bigint, PRIMARY KEY(id));"

$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "CREATE SCHEMA public2;"
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "CREATE TABLE public2.databasechangelog (id bigint, PRIMARY KEY(id));"
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "CREATE TABLE public2.databasechangeloglock (id bigint, PRIMARY KEY(id));"
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "CREATE TABLE public2.dummy_test (id bigint, PRIMARY KEY(id));"
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "CREATE TABLE public2.tly (id bigint, PRIMARY KEY(id));"
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "CREATE TABLE public2.msx (id bigint, PRIMARY KEY(id));"
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "CREATE TABLE public2.gyh (id bigint, PRIMARY KEY(id));"
set -e
