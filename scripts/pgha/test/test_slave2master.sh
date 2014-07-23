#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

$PGBIN_PATH/psql postgres -h $SLAVE_HOST -p $SLAVE_DB_PORT -c "DELETE FROM msx;"

# insert against SLAVE server
echo 'insert 9 records to postgres.msx table on slave'
for k in $(seq 1 9)
do
    $PGBIN_PATH/psql postgres -h $SLAVE_HOST -p $SLAVE_DB_PORT -c "INSERT INTO msx (id) VALUES ($k);"
done

echo 'waiting for replication...'
sleep 5s

echo 'check streaming replication data on MASTER...'
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "SELECT max(id) FROM msx"
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "SELECT count('x') FROM msx"

echo 'check streaming replication data on BCP...'
$PGBIN_PATH/psql postgres -h $BCP_HOST -p $SLAVE_DB_PORT -c "SELECT max(id) FROM msx"
$PGBIN_PATH/psql postgres -h $BCP_HOST -p $SLAVE_DB_PORT -c "SELECT count('x') FROM msx"

echo 'check londiste replication data on REPLICA...'
$PGBIN_PATH/psql postgres -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "SELECT max(id) FROM msx"
$PGBIN_PATH/psql postgres -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "SELECT count('x') FROM msx"