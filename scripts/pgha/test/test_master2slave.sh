#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "DELETE FROM tly;"

# insert against master server
echo 'insert 10 records to postgres.tly table on master'
for k in $(seq 1 10)
do
    $PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "INSERT INTO tly (id) VALUES ($k);"
done

echo 'waiting for replication...'
sleep 5s

echo 'check streaming replication data on SLAVE...'
$PGBIN_PATH/psql postgres -h $SLAVE_HOST -p $SLAVE_DB_PORT -c "SELECT max(id) FROM tly"
$PGBIN_PATH/psql postgres -h $SLAVE_HOST -p $SLAVE_DB_PORT -c "SELECT count('x') FROM tly"

echo 'check streaming replication data on BCP...'
$PGBIN_PATH/psql postgres -h $BCP_HOST -p $BCP_DB_PORT -c "SELECT max(id) FROM tly"
$PGBIN_PATH/psql postgres -h $BCP_HOST -p $BCP_DB_PORT -c "SELECT count('x') FROM tly"

echo 'check londiste replication data on REPLICA...'
$PGBIN_PATH/psql postgres -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "SELECT max(id) FROM tly"
$PGBIN_PATH/psql postgres -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "SELECT count('x') FROM tly"