#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

echo "kill skytools instance"
forceKillPid $SKYTOOL_PID_PATH

echo "waiting for replica catching up with master..."
while ! echo exit | psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "SELECT 'x' from pg_stat_replication where sent_location != replay_location;" | grep "(0 rows)"; do sleep 1 && echo "replica is catching up..."; done
echo "replica catch up with master!"

echo "promote replcia database to cut off streaming replication..."
touch $PROMOTE_TRIGGER_FILE

echo "waiting for replica promote"
while ! echo exit | [ -f $REPLICA_DATA_PATH/recovery.done ]; do sleep 1 && echo "replica is promoting..."; done
echo "replica promoted!"

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

    echo "drop leaf node if exist"
    londiste3 $config drop-node leaf_node_${db} > /dev/null 2>&1 || echo "node [leaf_node_${db}] does not exist"

    echo "create leaf node for database [$db]"
    londiste3 $config create-leaf leaf_node_${db} "dbname=$db host=$REPLICA_HOST port=$REPLICA_DB_PORT" --provider="dbname=$db host=$MASTER_HOST port=$MASTER_DB_PORT"

    echo "start worker for database [$db]"
    londiste3 -d $config worker > /dev/null 2>&1 &

    echo "subscribe all tables"
    londiste3 $config add-table --all --expect-sync

    echo "remove liquibase change log tables"
    londiste3 $config remove-table databasechangelog || echo "table missing"
    londiste3 $config remove-table databasechangeloglock || echo "table missing"
done
