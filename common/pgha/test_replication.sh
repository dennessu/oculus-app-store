#!/bin/bash
source set_env.sh

set +e
$PG_BIN/dropdb testdb -h $MASTER_SERVER -p $MASTER_PORT
$PG_BIN/createdb testdb -h $MASTER_SERVER -p $MASTER_PORT
$PG_BIN/psql testdb -h $MASTER_SERVER -p $MASTER_PORT -c "CREATE TABLE student (id bigint);"
set -e

 $PG_BIN/psql testdb -h $MASTER_SERVER -p $MASTER_PORT -c "DELETE FROM student;"

# insert against master server
for k in $(seq 1 50)
do
  $PG_BIN/psql testdb -h $MASTER_SERVER -p $MASTER_PORT -c "INSERT INTO student VALUES ($k);"
done

# insert against primary pgbouncer proxy
for k in $(seq 1 50)
do
  $PG_BIN/psql testdb -h $PRIMARY_PGBOUNCER_HOST -p $PRIMARY_PGBOUNCER_PORT -c "INSERT INTO student VALUES ($k);"
done

# insert against secondary pgbouncer proxy
for k in $(seq 1 50)
do
  $PG_BIN/psql testdb -h $SECONDARY_PGBOUNCER_HOST -p $SECONDARY_PGBOUNCER_PORT -c "INSERT INTO student VALUES ($k);"
done

echo 'waiting for replication...'
sleep 5s

echo 'check replication data on slave...'
$PG_BIN/psql testdb -h $SLAVE_SERVER -p $SLAVE_PORT -c "SELECT max(id) FROM student"
$PG_BIN/psql testdb -h $SLAVE_SERVER -p $SLAVE_PORT -c "SELECT count('x') FROM student"