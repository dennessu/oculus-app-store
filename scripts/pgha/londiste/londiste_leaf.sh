#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

echo "kill skytools instance"
forceKillPid $SKYTOOL_PID_PATH

echo "waiting for replica catching up with master..."
while ! echo exit | psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "SELECT 'x' from pg_stat_replication where sent_location != replay_location;" | grep "(0 rows)"; do sleep 1 && echo "slave is catching up..."; done
echo "replica catch up with slave!"

echo "promote replcia database to cut off streaming replication..."
touch $PROMOTE_TRIGGER_FILE

echo "waiting for straming replication cut off"
#TODO: seek for a better solution
sleep 5s

for db in ${REPLICA_DATABASES[@]}
do
    config=$SKYTOOL_CONFIG_PATH/${db}_leaf.ini

    echo "drop root node"
    set +e
    $PGBIN_PATH/psql $db -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "DROP SCHEMA pgq CASCADE;"
    $PGBIN_PATH/psql $db -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "DROP SCHEMA pgq_ext CASCADE;"
    $PGBIN_PATH/psql $db -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "DROP SCHEMA pgq_node CASCADE;"
    $PGBIN_PATH/psql $db -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "DROP SCHEMA londiste CASCADE;"
    set -e

    echo "create root node for database [$db]"
    londiste3 $config create-leaf leaf_node_${db} "dbname=$db host=$REPLICA_HOST port=$REPLICA_DB_PORT" --provider="dbname=$db host=$MASTER_HOST port=$MASTER_DB_PORT"

    echo "start worker for database [$db]"
    londiste3 -d $config worker > /dev/null 2>&1 &

    echo "subscribe all tables"
    londiste3 $config add-table --all

    #TODO: unsubscribe liquibase related tables
done
