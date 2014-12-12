#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "DELETE FROM tly;"
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "DELETE FROM public2.tly;"

# insert against master server
echo 'insert 10 records to tly table on master'
for k in $(seq 1 10)
do
    $PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "INSERT INTO public.tly (id) VALUES ($k);"
    $PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "INSERT INTO public2.tly (id) VALUES ($k);"
done

TIMESTAMP=$(($(date +%s%N)/1000000))
echo "insert timestamp to tly $TIMESTAMP"
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "INSERT INTO tly (id) VALUES ($TIMESTAMP);"
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "INSERT INTO public2.tly (id) VALUES ($TIMESTAMP);"

echo 'waiting for replication...'
sleep 5s

echo 'check streaming replication data on SLAVE...'
$PGBIN_PATH/psql postgres -h $SLAVE_HOST -p $SLAVE_DB_PORT -c 'SELECT max(id) as "slave max tly.id" FROM tly'
$PGBIN_PATH/psql postgres -h $SLAVE_HOST -p $SLAVE_DB_PORT -c 'SELECT count(1) as "slave tly count" FROM tly'
$PGBIN_PATH/psql postgres -h $SLAVE_HOST -p $SLAVE_DB_PORT -c 'SELECT max(id) as "slave max public2.tly.id" FROM public2.tly'
$PGBIN_PATH/psql postgres -h $SLAVE_HOST -p $SLAVE_DB_PORT -c 'SELECT count(1) as "slave public2.tly count" FROM public2.tly'

echo 'check streaming replication data on BCP...'
$PGBIN_PATH/psql postgres -h $BCP_HOST -p $BCP_DB_PORT -c 'SELECT max(id) as "bcp max tly.id" FROM tly'
$PGBIN_PATH/psql postgres -h $BCP_HOST -p $BCP_DB_PORT -c 'SELECT count(1) as "bcp tly count" FROM tly'
$PGBIN_PATH/psql postgres -h $BCP_HOST -p $BCP_DB_PORT -c 'SELECT max(id) as "bcp max public2.tly.id" FROM public2.tly'
$PGBIN_PATH/psql postgres -h $BCP_HOST -p $BCP_DB_PORT -c 'SELECT count(1) as "bcp public2.tly count" FROM public2.tly'

echo 'check londiste replication data on REPLICA...'
$PGBIN_PATH/psql postgres -h $REPLICA_HOST -p $REPLICA_DB_PORT -c 'SELECT max(id) as "replica max tly.id" FROM tly'
$PGBIN_PATH/psql postgres -h $REPLICA_HOST -p $REPLICA_DB_PORT -c 'SELECT count(1) as "replica tly count" FROM tly'
$PGBIN_PATH/psql postgres -h $REPLICA_HOST -p $REPLICA_DB_PORT -c 'SELECT max(id) as "replica max public2.tly.id" FROM public2.tly'
$PGBIN_PATH/psql postgres -h $REPLICA_HOST -p $REPLICA_DB_PORT -c 'SELECT count(1) as "replica public2.tly count" FROM public2.tly'