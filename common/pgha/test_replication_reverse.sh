#!/bin/bash
source set_env.sh

set +e
$PG_BIN/dropdb testdb2 -h $SLAVE_SERVER -p $SLAVE_PORT
$PG_BIN/createdb testdb2 -h $SLAVE_SERVER -p $SLAVE_PORT
$PG_BIN/psql testdb2 -h $SLAVE_SERVER -p $SLAVE_PORT -c "CREATE TABLE teacher (id bigint);"
set -e

# insert against master server
for k in $(seq 1 25)
do
  $PG_BIN/psql testdb2 -h $SLAVE_SERVER -p $SLAVE_PORT -c "INSERT INTO teacher VALUES ($k);"
done

# insert against primary pgbouncer proxy
for k in $(seq 1 25)
do
  $PG_BIN/psql testdb2 -h $PRIMARY_PGBOUNCER_HOST -p $PRIMARY_PGBOUNCER_PORT -c "INSERT INTO teacher VALUES ($k);"
done

# insert against secondary pgbouncer proxy
for k in $(seq 1 25)
do
  $PG_BIN/psql testdb2 -h $SECONDARY_PGBOUNCER_HOST -p $SECONDARY_PGBOUNCER_PORT -c "INSERT INTO teacher VALUES ($k);"
done

echo 'waiting for replication...'
sleep 5s

echo 'check replication data on old master...'
$PG_BIN/psql testdb2 -h $MASTER_SERVER -p $MASTER_PORT -c "SELECT max(id) FROM teacher"
$PG_BIN/psql testdb2 -h $SLAVE_SERVER -p $SLAVE_PORT -c "SELECT count('x') FROM teacher"