#!/bin/bash
source set_env.sh

set +e
$PG_BIN/dropdb testdb2 -h $SLAVE_SERVER -p $SLAVE_PORT
set -e

$PG_BIN/createdb testdb2 -h $SLAVE_SERVER -p $SLAVE_PORT
$PG_BIN/psql testdb2 -h $SLAVE_SERVER -p $SLAVE_PORT -c "CREATE TABLE teacher (id bigint);"

for k in $(seq 1 100)
do
  $PG_BIN/psql testdb2 -h $SLAVE_SERVER -p $SLAVE_PORT -c "INSERT INTO teacher VALUES ($k);"
done

echo 'waiting for replication...'
sleep 3s

echo 'check replication data on old master...'
$PG_BIN/psql testdb2 -h $MASTER_SERVER -p $MASTER_PORT -c "SELECT max(id) FROM teacher"