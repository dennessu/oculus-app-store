#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

echo "[LONDISTE][REPLICA] generate leaf configuration"
$DEPLOYMENT_PATH/londiste/londiste_config_leaf.sh

ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$MASTER_HOST << ENDSSH
    source $DEPLOYMENT_PATH/util/common.sh

    echo "[LONDISTE][MASTER] stop primary pgbouncer proxy"
    forceKill $PGBOUNCER_PORT

    echo "[LONDISTE][MASTER] kill skytools instance"
    forceKillPid $SKYTOOL_PID_PATH
ENDSSH

ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$SLAVE_HOST << ENDSSH
    source $DEPLOYMENT_PATH/util/common.sh

    echo "[LONDISTE][SLAVE] stop secondary pgbouncer proxy"
    forceKill $PGBOUNCER_PORT
ENDSSH

echo "[LONDISTE][REPLICA] kill skytools instance"
forceKillPid $SKYTOOL_PID_PATH

#echo "[LONDISTE][REPLICA] copy unarchived log files"
#rsync -e "ssh -o StrictHostKeyChecking=no" -azhv $DEPLOYMENT_ACCOUNT@$MASTER_HOST:$MASTER_DATA_PATH/pg_xlog/* $ARCHIVE_PATH

xlog_location=`psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "SELECT pg_current_xlog_location();" -t | tr -d ' '`
echo "[LONDISTE][REPLICA] current xlog location is [$xlog_location]"

echo "[LONDISTE][REPLICA] waiting for replica catching up with master"
while [ `psql postgres -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "SELECT pg_xlog_location_diff(pg_last_xlog_replay_location(), '$xlog_location');" -t | tr -d ' '` -lt 0 ]
do
    sleep 1 && echo "[LONDISTE][REPLICA] replica is catching up..."; 
done
echo "[LONDISTE][REPLICA] replica catch up with master!"

echo "[LONDISTE][REPLICA] promote replcia database to cut off streaming replication"
touch $PROMOTE_TRIGGER_FILE

echo "[LONDISTE][REPLICA] waiting for replica promote"
while ! echo exit | psql postgres -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "SELECT pg_is_in_recovery();" -t | grep "f";
do
   sleep 1 && echo "[LONDISTE][REPLICA] replica is promoting...";
done
echo "[LONDISTE][REPLICA] replica promoted!"

echo "[LONDISTE][REPLICA] ensure replica can be written"
while ! echo exit | psql postgres -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "insert into dummy_test values($(($(date +%s%N)/1000000)));" || echo "ERROR" | grep -v "ERROR";
do
   sleep 1 && echo "[LONDISTE][REPLICA] replica is still in read-only status...";
done
echo "[LONDISTE][REPLICA] replica can be written"

for db in ${REPLICA_DATABASES[@]}
do
    config=$SKYTOOL_CONFIG_PATH/${db}_leaf.ini

    echo "[LONDISTE][REPLICA] drop root node"
    set +e
    psql $db -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "DROP SCHEMA pgq CASCADE;"
    psql $db -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "DROP SCHEMA pgq_ext CASCADE;"
    psql $db -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "DROP SCHEMA pgq_node CASCADE;"
    psql $db -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "DROP SCHEMA londiste CASCADE;"
    set -e

    SCHEMA=`$PGBIN_PATH/psql ${db} -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "\dn" -t | tr -d ' ' | cut -d '|' -f 1 | sed '/^$/d'`

    echo "[LONDISTE][REPLICA] drop leaf node if exist"
    londiste3 $config drop-node leaf_node_${db} > /dev/null 2>&1 || echo "node [leaf_node_${db}] does not exist"

ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$MASTER_HOST << ENDSSH
    echo "[LONDISTE][MASTER] drop leaf node if exist"
    londiste3 $SKYTOOL_CONFIG_PATH/${db}_root.ini drop-node leaf_node_${db} > /dev/null 2>&1 || echo "node [leaf_node_${db}] does not exist"
ENDSSH

    echo "[LONDISTE][REPLICA] create leaf node for database [$db]"
    londiste3 $config create-leaf leaf_node_${db} "dbname=$db host=$REPLICA_HOST port=$REPLICA_DB_PORT" --provider="dbname=$db host=$MASTER_HOST port=$MASTER_DB_PORT"

    echo "[LONDISTE][REPLICA] start worker for database [$db]"
    londiste3 -d $config worker > /dev/null 2>&1 &

    echo "[LONDISTE][REPLICA] subscribe all tables"
    londiste3 $config add-table --all --expect-sync

    echo "[LONDISTE][REPLICA] remove liquibase change log tables"
    echo "$SCHEMA" | while read schema
    do 
       londiste3 $config remove-table ${schema}.databasechangelog ||  echo "WARN: table [${schema}.databasechangelog] missing"
       londiste3 $config remove-table ${schema}.databasechangeloglock || echo "WARN: table [${schema}.databasechangelog] missing"
    done
done

ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$MASTER_HOST << ENDSSH
    for db in ${REPLICA_DATABASES[@]}
    do
        config=$SKYTOOL_CONFIG_PATH/\${db}_root.ini

        echo "[LONDISTE][MASTER] start worker for database [\$db]"
        londiste3 -d \$config worker > /dev/null 2>&1 &
    done

    echo "[LONDISTE][MASTER] start pgqd deamon"
    $DEPLOYMENT_PATH/londiste/londiste_pgqd.sh

    echo "[LONDISTE][MASTER] start primary pgbouncer proxy"
    $DEPLOYMENT_PATH/pgbouncer/pgbouncer_master.sh
ENDSSH

ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$SLAVE_HOST << ENDSSH
    echo "[LONDISTE][SLAVE] start secondary pgbouncer proxy"
    $DEPLOYMENT_PATH/pgbouncer/pgbouncer_master.sh
ENDSSH

echo "[LONDISTE][REPLICA] start pgqd deamon"
$DEPLOYMENT_PATH/londiste/londiste_pgqd.sh

