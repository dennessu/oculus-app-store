#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

ssh $DEPLOYMENT_ACCOUNT@$MASTER_HOST << ENDSSH
    source $DEPLOYMENT_PATH/util/common.sh

    echo "[MASTER] stop primary pgbouncer proxy"
    forceKill $PGBOUNCER_PORT

    echo "[MASTER] kill skytools instance"
    forceKillPid $SKYTOOL_PID_PATH
ENDSSH

ssh $DEPLOYMENT_ACCOUNT@$SLAVE_HOST << ENDSSH
    source $DEPLOYMENT_PATH/util/common.sh

    echo "[SLAVE] stop secondary pgbouncer proxy"
    forceKill $PGBOUNCER_PORT
ENDSSH

echo "[REPLICA] kill skytools instance"
forceKillPid $SKYTOOL_PID_PATH

echo "[REPLICA] copy unarchived log files"
rsync -azhv $DEPLOYMENT_ACCOUNT@$MASTER_HOST:$MASTER_DATA_PATH/pg_xlog/* $REPLICA_ARCHIVE_PATH

echo "[REPLICA] waiting for replica catching up with master"
xlog_location=`psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "SELECT pg_current_xlog_location();" -t | tr -d ' '`
echo "[REPLICA] current xlog location is [$xlog_location]"

while [ `psql postgres -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "SELECT pg_xlog_location_diff(pg_last_xlog_replay_location(), '$xlog_location');" -t | tr -d ' '` -lt 0 ]
do
    sleep 1 && echo "[REPLICA] replica is catching up..."; 
done
echo "[REPLICA] replica catch up with master!"

echo "[REPLICA] promote replcia database to cut off streaming replication"
touch $PROMOTE_TRIGGER_FILE

echo "[REPLICA] waiting for replica promote"
while ! echo exit | psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "SELECT pg_is_in_recovery();" -t | grep "f"; do sleep 1 && echo "[REPLICA] replica is promoting..."; done
echo "[REPLICA] replica promoted!"

echo "[REPLICA] force wait beforing writing"
sleep 5s

for db in ${REPLICA_DATABASES[@]}
do
    config=$SKYTOOL_CONFIG_PATH/${db}_leaf.ini

    echo "[REPLICA] drop root node"
    set +e
    $PGBIN_PATH/psql $db -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "DROP SCHEMA pgq CASCADE;"
    $PGBIN_PATH/psql $db -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "DROP SCHEMA pgq_ext CASCADE;"
    $PGBIN_PATH/psql $db -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "DROP SCHEMA pgq_node CASCADE;"
    $PGBIN_PATH/psql $db -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "DROP SCHEMA londiste CASCADE;"
    set -e

    echo "[REPLICA] drop leaf node if exist"
    londiste3 $config drop-node leaf_node_${db} > /dev/null 2>&1 || echo "node [leaf_node_${db}] does not exist"

    echo "[REPLICA] create leaf node for database [$db]"
    londiste3 $config create-leaf leaf_node_${db} "dbname=$db host=$REPLICA_HOST port=$REPLICA_DB_PORT" --provider="dbname=$db host=$MASTER_HOST port=$MASTER_DB_PORT"

    echo "[REPLICA] start worker for database [$db]"
    londiste3 -d $config worker > /dev/null 2>&1 &

    echo "[REPLICA] subscribe all tables"
    londiste3 $config add-table --all --expect-sync

    echo "[REPLICA] remove liquibase change log tables"
    londiste3 $config remove-table databasechangelog || echo "table missing"
    londiste3 $config remove-table databasechangeloglock || echo "table missing"
done

ssh $DEPLOYMENT_ACCOUNT@$MASTER_HOST << ENDSSH
    for db in ${REPLICA_DATABASES[@]}
    do
        config=$SKYTOOL_CONFIG_PATH/\${db}_root.ini

        echo "[MASTER] start worker for database [\$db]"
        londiste3 -d \$config worker > /dev/null 2>&1 &
    done

    echo "[MASTER] start pgqd deamon"
    $DEPLOYMENT_PATH/londiste/londiste_pgqd.sh

    echo "[MASTER] start primary pgbouncer proxy"
    $DEPLOYMENT_PATH/pgbouncer/pgbouncer_master.sh
ENDSSH

ssh $DEPLOYMENT_ACCOUNT@$SLAVE_HOST << ENDSSH
    echo "[SLAVE] start secondary pgbouncer proxy"
    $DEPLOYMENT_PATH/pgbouncer/pgbouncer_master.sh
ENDSSH
