#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

echo "[SLAVE] stop traffic for failover"

echo "[SLAVE] stop secondary pgbouncer proxy"
forceKill $PGBOUNCER_PORT

ssh $DEPLOYMENT_ACCOUNT@$MASTER_HOST << ENDSSH
    source $DEPLOYMENT_PATH/util/common.sh

    echo "[MASTER] stop primary pgbouncer proxy"
    forceKill $PGBOUNCER_PORT

    echo "[MASTER] kill skytools instances"
    forceKillPid $SKYTOOL_PID_PATH
ENDSSH

ssh $DEPLOYMENT_ACCOUNT@$REPLICA_HOST << ENDSSH
    source $DEPLOYMENT_PATH/util/common.sh

    echo "[REPLICA] kill skytools instances"
    forceKillPid $SKYTOOL_PID_PATH
ENDSSH

echo "[SLAVE] waiting for slave catching up with master"
while ! echo exit | psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "SELECT 'x' from pg_stat_replication where sent_location != replay_location;" -t | grep -v "x"; do sleep 1 && echo "[SLAVE] slave is catching up..."; done
echo "[SLAVE] slave catch up with master!"

ssh $DEPLOYMENT_ACCOUNT@$MASTER_HOST << ENDSSH
    echo "[MASTER] gracefully shutdown master database"
    $PGBIN_PATH/pg_ctl stop -m fast -D $MASTER_DATA_PATH
ENDSSH

echo "[SLAVE] copy unarchived log files"
rsync -azhv $DEPLOYMENT_ACCOUNT@$MASTER_HOST:$MASTER_DATA_PATH/pg_xlog/* $SLAVE_ARCHIVE_PATH

echo "[SLAVE] promote slave database to take traffic"
touch $PROMOTE_TRIGGER_FILE

echo "[SLAVE] waiting for slave promote"
while ! echo exit | psql postgres -h $SLAVE_HOST -p $SLAVE_DB_PORT -c "SELECT pg_is_in_recovery();" -t | grep "f"; do sleep 1 && echo "[SLAVE] slave is promoting..."; done
echo "[SLAVE] slave promoted!"

ssh $DEPLOYMENT_ACCOUNT@$MASTER_HOST << ENDSSH
    echo "[MASTER] configure recovery.conf for master"
    cat > $MASTER_DATA_PATH/recovery.conf <<EOF
recovery_target_timeline = 'latest'
restore_command = 'cp $MASTER_ARCHIVE_PATH/%f %p'
standby_mode = 'on'
primary_conninfo = 'user=$PGUSER host=$SLAVE_HOST port=$SLAVE_DB_PORT sslmode=prefer sslcompression=1 krbsrvname=$PGUSER'
trigger_file = '$PROMOTE_TRIGGER_FILE'
EOF

    echo "[MASTER] start master database"
    $PGBIN_PATH/pg_ctl -D $MASTER_DATA_PATH start > /dev/null 2>&1 &

    while ! echo exit | nc $MASTER_HOST $MASTER_DB_PORT; do sleep 1 && echo "[MASTER] waiting for master database start up..."; done
    echo "[MASTER] master database started successfully!"
ENDSSH

echo "[SLAVE] generate londiste root configuration"
$DEPLOYMENT_PATH/londiste/londiste_config_root.sh

for db in ${REPLICA_DATABASES[@]}
do
    config=$SKYTOOL_CONFIG_PATH/${db}_root.ini

    echo "[SLAVE] update root node location"
    psql ${db} -h $SLAVE_HOST -p $SLAVE_DB_PORT -c "update pgq_node.node_location set node_location = 'dbname=${db} host=$SLAVE_HOST port=$SLAVE_DB_PORT' where queue_name = 'queue_${db}' and node_name = 'root_node_${db}';"

    echo "[SLAVE] start londiste worker for database [$db]"
    londiste3 -d $config worker > /dev/null 2>&1 &
done

echo "[SLAVE] start pgqd deamon on slave"
$DEPLOYMENT_PATH/londiste/londiste_pgqd.sh

ssh $DEPLOYMENT_ACCOUNT@$REPLICA_HOST << ENDSSH
    for db in ${REPLICA_DATABASES[@]}
    do
        config=$SKYTOOL_CONFIG_PATH/\${db}_leaf.ini

        echo "[REPLICA] update root node location"
        psql ${db} -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "update pgq_node.node_location set node_location = 'dbname=${db} host=$SLAVE_HOST port=$SLAVE_DB_PORT' where queue_name = 'queue_${db}' and node_name = 'root_node_${db}';"

        echo "[REPLICA] start worker for database [\$db]"
        londiste3 -d \$config worker > /dev/null 2>&1 &
    done

    echo "[REPLICA] start pgqd deamon"
	$DEPLOYMENT_PATH/londiste/londiste_pgqd.sh
ENDSSH
