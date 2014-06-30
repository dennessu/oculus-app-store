#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

echo "[FAILOVER][SLAVE] stop traffic for failover"

echo "[FAILOVER][SLAVE] stop secondary pgbouncer proxy"
forceKill $PGBOUNCER_PORT

ssh $DEPLOYMENT_ACCOUNT@$MASTER_HOST << ENDSSH
    source $DEPLOYMENT_PATH/util/common.sh

    echo "[FAILOVER][MASTER] stop primary pgbouncer proxy"
    forceKill $PGBOUNCER_PORT

    echo "[FAILOVER][MASTER] kill skytools instances"
    forceKillPid $SKYTOOL_PID_PATH
ENDSSH

ssh $DEPLOYMENT_ACCOUNT@$REPLICA_HOST << ENDSSH
    source $DEPLOYMENT_PATH/util/common.sh

    echo "[FAILOVER][REPLICA] kill skytools instances"
    forceKillPid $SKYTOOL_PID_PATH
ENDSSH

xlog_location=`psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "SELECT pg_current_xlog_location();" -t | tr -d ' '`
echo "[FAILOVER][MASTER] current xlog location is [$xlog_location]"

ssh $DEPLOYMENT_ACCOUNT@$MASTER_HOST << ENDSSH
    echo "[FAILOVER][MASTER] gracefully shutdown master database"
    $PGBIN_PATH/pg_ctl stop -m fast -D $MASTER_DATA_PATH
ENDSSH

echo "[FAILOVER][SLAVE] copy unarchived log files"
rsync -azhv $DEPLOYMENT_ACCOUNT@$MASTER_HOST:$MASTER_DATA_PATH/pg_xlog/* $SLAVE_ARCHIVE_PATH

echo "[FAILOVER][SLAVE] waiting for slave catching up with master"
while [ `psql postgres -h $SLAVE_HOST -p $SLAVE_DB_PORT -c "SELECT pg_xlog_location_diff(pg_last_xlog_replay_location(), '$xlog_location');" -t | tr -d ' '` -lt 0 ]
do
    sleep 1 && echo "[SLAVE] slave is catching up..."; 
done
echo "[FAILOVER][SLAVE] slave catch up with master!"

echo "[FAILOVER][SLAVE] promote slave database to take traffic"
touch $PROMOTE_TRIGGER_FILE

echo "[FAILOVER][SLAVE] waiting for slave promote"
while ! echo exit | psql postgres -h $SLAVE_HOST -p $SLAVE_DB_PORT -c "SELECT pg_is_in_recovery();" -t | grep "f"; do sleep 1 && echo "[SLAVE] slave is promoting..."; done
echo "[FAILOVER][SLAVE] slave promoted!"

echo "[FAILOVER][SLAVE] force wait beforing writing"
sleep 5s

ssh $DEPLOYMENT_ACCOUNT@$MASTER_HOST << ENDSSH
    echo "[FAILOVER][MASTER] configure recovery.conf for master"
    cat > $MASTER_DATA_PATH/recovery.conf <<EOF
recovery_target_timeline = 'latest'
restore_command = 'cp $MASTER_ARCHIVE_PATH/%f %p'
standby_mode = 'on'
primary_conninfo = 'user=$PGUSER host=$SLAVE_HOST port=$SLAVE_DB_PORT sslmode=prefer sslcompression=1 krbsrvname=$PGUSER'
trigger_file = '$PROMOTE_TRIGGER_FILE'
EOF

    echo "[FAILOVER][MASTER] start master database"
    $PGBIN_PATH/pg_ctl -D $MASTER_DATA_PATH -l "${MASTER_LOG_PATH}/postgresql-$(date +%Y.%m.%d.%S.%N).log" start > /dev/null 2>&1 &

    while ! echo exit | nc $MASTER_HOST $MASTER_DB_PORT;
    do
        sleep 1 && echo "[FAILOVER][MASTER] waiting for master database start up...";
    done
    echo "[FAILOVER][MASTER] master database started successfully!"
ENDSSH

echo "[SLAVE] generate londiste root configuration"
$DEPLOYMENT_PATH/londiste/londiste_config_root.sh

for db in ${REPLICA_DATABASES[@]}
do
    config=$SKYTOOL_CONFIG_PATH/${db}_root.ini

    echo "[FAILOVER][SLAVE] update root node location"
    psql ${db} -h $SLAVE_HOST -p $SLAVE_DB_PORT -c "update pgq_node.node_location set node_location = 'dbname=${db} host=$SLAVE_HOST port=$SLAVE_DB_PORT' where queue_name = 'queue_${db}' and node_name = 'root_node_${db}';"

    echo "[FAILOVER][SLAVE] start londiste worker for database [$db]"
    londiste3 -d $config worker > /dev/null 2>&1 &
done

echo "[FAILOVER][SLAVE] start pgqd deamon on slave"
$DEPLOYMENT_PATH/londiste/londiste_pgqd.sh

ssh $DEPLOYMENT_ACCOUNT@$REPLICA_HOST << ENDSSH
    for db in ${REPLICA_DATABASES[@]}
    do
        config=$SKYTOOL_CONFIG_PATH/\${db}_leaf.ini

        echo "[FAILOVER][REPLICA] update root node location"
        psql \${db} -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "update pgq_node.node_location set node_location = 'dbname=\${db} host=$SLAVE_HOST port=$SLAVE_DB_PORT' where queue_name = 'queue_\${db}' and node_name = 'root_node_\${db}';"

        echo "[FAILOVER][REPLICA] start worker for database [\$db]"
        londiste3 -d \$config worker > /dev/null 2>&1 &
    done

    echo "[FAILOVER][REPLICA] start pgqd deamon"
	$DEPLOYMENT_PATH/londiste/londiste_pgqd.sh
ENDSSH
