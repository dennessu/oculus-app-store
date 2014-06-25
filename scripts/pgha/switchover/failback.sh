#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

echo "[MASTER] stop traffic for failback"

echo "[MASTER] stop primary pgbouncer proxy"
forceKill $PGBOUNCER_PORT

ssh $DEPLOYMENT_ACCOUNT@$SLAVE_HOST << ENDSSH
    source $DEPLOYMENT_PATH/util/common.sh

    echo "[SLAVE] stop secondary pgbouncer proxy"
    forceKill $PGBOUNCER_PORT

    echo "[SLAVE] kill skytools instances"
    forceKillPid $SKYTOOL_PID_PATH
ENDSSH

ssh $DEPLOYMENT_ACCOUNT@$REPLICA_HOST << ENDSSH
    source $DEPLOYMENT_PATH/util/common.sh

    echo "[REPLICA] kill skytools instances"
    forceKillPid $SKYTOOL_PID_PATH
ENDSSH

echo "[MASTER] copy unarchived log files"
rsync -azhv $DEPLOYMENT_ACCOUNT@$SLAVE_HOST:$SLAVE_DATA_PATH/pg_xlog/* $MASTER_ARCHIVE_PATH

echo "[MASTER] waiting for master catching up with slave"
xlog_location=`psql postgres -h $SLAVE_HOST -p $SLAVE_DB_PORT -c "SELECT pg_current_xlog_location();" -t | tr -d ' '`
echo "[MASTER] current xlog location is [$xlog_location]"

while [ `psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "SELECT pg_xlog_location_diff(pg_last_xlog_replay_location(), '$xlog_location');" -t | tr -d ' '` -lt 0 ]
do
    sleep 1 && echo "[MASTER] slave is catching up..."; 
done
echo "[MASTER] master catch up with slave!"

ssh $DEPLOYMENT_ACCOUNT@$SLAVE_HOST << ENDSSH
    echo "[SLAVE] gracefully shutdown slave database"
    $PGBIN_PATH/pg_ctl stop -m fast -D $SLAVE_DATA_PATH
ENDSSH

echo "[MASTER] promote master database to take traffic"
touch $PROMOTE_TRIGGER_FILE

echo "[MASTER] waiting for master promote"
while ! echo exit | psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "SELECT pg_is_in_recovery();" -t | grep "f"; do sleep 1 && echo "[MASTER] master is promoting..."; done
echo "[MASTER] master promoted!"

echo "[MASTER] force wait beforing writing"
sleep 5s

ssh $DEPLOYMENT_ACCOUNT@$SLAVE_HOST << ENDSSH
    echo "[SLAVE] configure recovery.conf for slave"
    cat > $SLAVE_DATA_PATH/recovery.conf <<EOF
recovery_target_timeline = 'latest'
restore_command = 'cp $SLAVE_ARCHIVE_PATH/%f %p'
standby_mode = 'on'
primary_conninfo = 'user=$PGUSER host=$MASTER_HOST port=$MASTER_DB_PORT sslmode=prefer sslcompression=1 krbsrvname=$PGUSER'
trigger_file = '$PROMOTE_TRIGGER_FILE'
EOF

    echo "[SLAVE] start slave database"
    $PGBIN_PATH/pg_ctl -D $SLAVE_DATA_PATH start > /dev/null 2>&1 &

    while ! echo exit | nc $SLAVE_HOST $SLAVE_DB_PORT; do sleep 1 && echo "[SLAVE] waiting for slave database start up..."; done
    echo "[SLAVE] slave database started successfully!"
ENDSSH

echo "[MASTER] generate londiste root configuration"
$DEPLOYMENT_PATH/londiste/londiste_config_root.sh

for db in ${REPLICA_DATABASES[@]}
do
    config=$SKYTOOL_CONFIG_PATH/${db}_root.ini

    echo "[MASTER] update root node location"
    psql ${db} -h $MASTER_HOST -p $MASTER_DB_PORT -c "update pgq_node.node_location set node_location = 'dbname=${db} host=$MASTER_HOST port=$MASTER_DB_PORT' where queue_name = 'queue_${db}' and node_name = 'root_node_${db}';"

    echo "[MASTER] start londiste worker for database [$db]"
    londiste3 -d $config worker > /dev/null 2>&1 &
done

echo "[MASTER] start pgqd deamon on slave"
$DEPLOYMENT_PATH/londiste/londiste_pgqd.sh

ssh $DEPLOYMENT_ACCOUNT@$REPLICA_HOST << ENDSSH
    for db in ${REPLICA_DATABASES[@]}
    do
        config=$SKYTOOL_CONFIG_PATH/\${db}_leaf.ini

        echo "[REPLICA] update root node location"
        psql \${db} -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "update pgq_node.node_location set node_location = 'dbname=\${db} host=$MASTER_HOST port=$MASTER_DB_PORT' where queue_name = 'queue_\${db}' and node_name = 'root_node_\${db}';"

        echo "[REPLICA] start worker for database [\$db]"
        londiste3 -d \$config worker > /dev/null 2>&1 &
    done

    echo "[REPLICA] start pgqd deamon"
	$DEPLOYMENT_PATH/londiste/londiste_pgqd.sh
ENDSSH
