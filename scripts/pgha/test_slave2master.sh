#!/bin/bash
source common.sh

set +e
$PGBIN_PATH/dropdb testdb -h $SLAVE_HOST -p $SLAVE_DB_PORT
$PGBIN_PATH/createdb testdb -h $SLAVE_HOST -p $SLAVE_DB_PORT
$PGBIN_PATH/psql testdb -h $SLAVE_HOST -p $SLAVE_DB_PORT -c "CREATE TABLE teacher (id bigint);"
set -e

$PGBIN_PATH/psql testdb -h $SLAVE_HOST -p $SLAVE_DB_PORT -c "DELETE FROM teacher;"

# insert against SLAVE server
for k in $(seq 1 49)
do
  $PGBIN_PATH/psql testdb -h $SLAVE_HOST -p $SLAVE_DB_PORT -c "INSERT INTO teacher VALUES ($k);"
done

# insert against primary pgbouncer proxy
for k in $(seq 1 49)
do
  $PGBIN_PATH/psql testdb -h $PRIMARY_PGBOUNCER_HOST -p $PGBOUNCER_PORT -c "INSERT INTO teacher VALUES ($k);"
done

# insert against secondary pgbouncer proxy
for k in $(seq 1 49)
do
  $PGBIN_PATH/psql testdb -h $SECONDARY_PGBOUNCER_HOST -p $PGBOUNCER_PORT -c "INSERT INTO teacher VALUES ($k);"
done

echo 'waiting for replication...'
sleep 5s

echo 'check replication data on MASTER...'
$PGBIN_PATH/psql testdb -h $MASTER_HOST -p $MASTER_DB_PORT -c "SELECT max(id) FROM teacher"
$PGBIN_PATH/psql testdb -h $MASTER_HOST -p $MASTER_DB_PORT -c "SELECT count('x') FROM teacher"