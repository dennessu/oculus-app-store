#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

$PGBIN_PATH/psql postgres -h $SLAVE_HOST -p $SLAVE_DB_PORT -c "DELETE FROM teacher;"

# insert against SLAVE server
for k in $(seq 1 9)
do
    $PGBIN_PATH/psql postgres -h $SLAVE_HOST -p $SLAVE_DB_PORT -c "INSERT INTO teacher VALUES ($k);"
done

# insert against primary pgbouncer proxy
#for k in $(seq 10 18)
#do
#    $PGBIN_PATH/psql postgres -h $PRIMARY_PGBOUNCER_HOST -p $PGBOUNCER_PORT -c "INSERT INTO teacher VALUES ($k);"
#done

# insert against secondary pgbouncer proxy
#for k in $(seq 19 27)
#do
#    $PGBIN_PATH/psql postgres -h $SECONDARY_PGBOUNCER_HOST -p $PGBOUNCER_PORT -c "INSERT INTO teacher VALUES ($k);"
#done

echo 'waiting for replication...'
sleep 5s

echo 'check replication data on MASTER...'
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "SELECT max(id) FROM teacher"
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "SELECT count('x') FROM teacher"

echo 'check replication data on REPLICA...'
$PGBIN_PATH/psql postgres -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "SELECT max(id) FROM teacher"
$PGBIN_PATH/psql postgres -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "SELECT count('x') FROM teacher"