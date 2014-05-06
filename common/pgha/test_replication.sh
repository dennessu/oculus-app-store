#!/bin/bash
source set_env.sh

set +e
$PG_BIN/dropdb testdb -h $MASTER_SERVER -p $MASTER_PORT
set -e

$PG_BIN/createdb testdb -h $MASTER_SERVER -p $MASTER_PORT
$PG_BIN/psql testdb -h $MASTER_SERVER -p $MASTER_PORT -c "CREATE TABLE student (id bigint);"

for k in $(seq 1 100)
do
  $PG_BIN/psql testdb -h $MASTER_SERVER -p $MASTER_PORT -c "INSERT INTO student VALUES ($k);"
done

echo 'waiting for replication...'
sleep 5s

echo 'check replication data on slave...'
$PG_BIN/psql testdb -h $SLAVE_SERVER -p $SLAVE_PORT -c "SELECT max(id) FROM student"