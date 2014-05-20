#!/bin/bash
source common.sh

set +e
$PGBIN_PATH/dropdb testdb -h $MASTER_HOST -p $MASTER_DB_PORT
$PGBIN_PATH/createdb testdb -h $MASTER_HOST -p $MASTER_DB_PORT
$PGBIN_PATH/psql testdb -h $MASTER_HOST -p $MASTER_DB_PORT -c "CREATE TABLE student (id bigint);"
set -e

$PGBIN_PATH/psql testdb -h $MASTER_HOST -p $MASTER_DB_PORT -c "DELETE FROM student;"

# insert against master server
for k in $(seq 1 50)
do
  $PGBIN_PATH/psql testdb -h $MASTER_HOST -p $MASTER_DB_PORT -c "INSERT INTO student VALUES ($k);"
done

# insert against primary pgbouncer proxy
for k in $(seq 1 50)
do
  $PGBIN_PATH/psql testdb -h $PRIMARY_PGBOUNCER_HOST -p $PRIMARY_PGBOUNCER_PORT -c "INSERT INTO student VALUES ($k);"
done

# insert against secondary pgbouncer proxy
for k in $(seq 1 50)
do
  $PGBIN_PATH/psql testdb -h $SECONDARY_PGBOUNCER_HOST -p $SECONDARY_PGBOUNCER_PORT -c "INSERT INTO student VALUES ($k);"
done

echo 'waiting for replication...'
sleep 5s

echo 'check replication data on slave...'
$PGBIN_PATH/psql testdb -h $SLAVE_SERVER -p $SLAVE_PORT -c "SELECT max(id) FROM student"
$PGBIN_PATH/psql testdb -h $SLAVE_SERVER -p $SLAVE_PORT -c "SELECT count('x') FROM student"