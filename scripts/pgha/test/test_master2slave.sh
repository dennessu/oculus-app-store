#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "DELETE FROM student;"

# insert against master server
for k in $(seq 1 10)
do
    $PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "INSERT INTO student VALUES ($k);"
done

# insert against primary pgbouncer proxy
for k in $(seq 11 20)
do
    $PGBIN_PATH/psql postgres -h $PRIMARY_PGBOUNCER_HOST -p $PGBOUNCER_PORT -c "INSERT INTO student VALUES ($k);"
done

# insert against secondary pgbouncer proxy
for k in $(seq 21 30)
do
    $PGBIN_PATH/psql postgres -h $SECONDARY_PGBOUNCER_HOST -p $PGBOUNCER_PORT -c "INSERT INTO student VALUES ($k);"
done

echo 'waiting for replication...'
sleep 5s

echo 'check replication data on SLAVE...'
$PGBIN_PATH/psql postgres -h $SLAVE_HOST -p $SLAVE_DB_PORT -c "SELECT max(id) FROM student"
$PGBIN_PATH/psql postgres -h $SLAVE_HOST -p $SLAVE_DB_PORT -c "SELECT count('x') FROM student"

echo 'check replication data on REPLICA...'
$PGBIN_PATH/psql postgres -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "SELECT max(id) FROM student"
$PGBIN_PATH/psql postgres -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "SELECT count('x') FROM student"