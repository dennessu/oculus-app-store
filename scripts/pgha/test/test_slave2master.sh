#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

$PGBIN_PATH/psql postgres -h $SLAVE_HOST -p $SLAVE_DB_PORT -c "DELETE FROM msx;"
$PGBIN_PATH/psql postgres -h $SLAVE_HOST -p $SLAVE_DB_PORT -c "DELETE FROM public2.msx;"

# insert against SLAVE server
echo 'insert 9 records to msx table on slave'
for k in $(seq 1 9)
do
    $PGBIN_PATH/psql postgres -h $SLAVE_HOST -p $SLAVE_DB_PORT -c "INSERT INTO msx (id) VALUES ($k);"
    $PGBIN_PATH/psql postgres -h $SLAVE_HOST -p $SLAVE_DB_PORT -c "INSERT INTO public2.msx (id) VALUES ($k);"
done

TIMESTAMP=$(($(date +%s%N)/1000000))
echo "insert timestamp to msx $TIMESTAMP"
$PGBIN_PATH/psql postgres -h $SLAVE_HOST -p $SLAVE_DB_PORT -c "INSERT INTO msx (id) VALUES ($TIMESTAMP);"
$PGBIN_PATH/psql postgres -h $SLAVE_HOST -p $SLAVE_DB_PORT -c "INSERT INTO public2.msx (id) VALUES ($TIMESTAMP);"

echo 'waiting for replication...'
sleep 5s

echo 'check streaming replication data on MASTER...'
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c 'SELECT max(id) as "slave max msx.id" FROM msx'
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c 'SELECT count(1) as "slave msx count" FROM msx'
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c 'SELECT max(id) as "slave max public2.msx.id" FROM public2.msx'
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c 'SELECT count(1) as "slave public2.msx count" FROM public2.msx'

echo 'check streaming replication data on BCP...'
$PGBIN_PATH/psql postgres -h $BCP_HOST -p $BCP_DB_PORT -c 'SELECT max(id) as "bcp max msx.id" FROM msx'
$PGBIN_PATH/psql postgres -h $BCP_HOST -p $BCP_DB_PORT -c 'SELECT count(1) as "bcp msx count" FROM msx'
$PGBIN_PATH/psql postgres -h $BCP_HOST -p $BCP_DB_PORT -c 'SELECT max(id) as "bcp max public2.msx.id" FROM public2.msx'
$PGBIN_PATH/psql postgres -h $BCP_HOST -p $BCP_DB_PORT -c 'SELECT count(1) as "bcp public2.msx count" FROM public2.msx'

echo 'check londiste replication data on REPLICA...'
$PGBIN_PATH/psql postgres -h $REPLICA_HOST -p $REPLICA_DB_PORT -c 'SELECT max(id) as "replica max msx.id" FROM msx'
$PGBIN_PATH/psql postgres -h $REPLICA_HOST -p $REPLICA_DB_PORT -c 'SELECT count(1) as "replica msx count" FROM msx'
$PGBIN_PATH/psql postgres -h $REPLICA_HOST -p $REPLICA_DB_PORT -c 'SELECT max(id) as "replica max public2.msx.id" FROM public2.msx'
$PGBIN_PATH/psql postgres -h $REPLICA_HOST -p $REPLICA_DB_PORT -c 'SELECT count(1) as "replica public2.msx count" FROM public2.msx'