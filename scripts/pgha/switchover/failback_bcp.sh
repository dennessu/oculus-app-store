#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

role=`getServerRole`
echo "The server is [$role]"

if [[ $role != "MASTER" ]] ; then
    echo "[REMEDY][$role] bcp failback script should be executed on [MASTER] server"
    exit
fi

echo "[FAILBACK][MASTER] stop traffic for failback"

echo "[FAILBACK][MASTER] stop primary pgbouncer proxy"
forceKill $PGBOUNCER_PORT

ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$BCP_HOST << ENDSSH
    source $DEPLOYMENT_PATH/util/common.sh

    echo "[FAILBACK][BCP] stop secondary pgbouncer proxy"
    forceKill $PGBOUNCER_PORT

    echo "[FAILBACK][BCP] kill skytools instances"
    forceKillPid $SKYTOOL_PID_PATH
ENDSSH

ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$REPLICA_HOST << ENDSSH
    source $DEPLOYMENT_PATH/util/common.sh

    echo "[FAILBACK][REPLICA] kill skytools instances"
    forceKillPid $SKYTOOL_PID_PATH
ENDSSH

xlog_location=`psql postgres -h $BCP_HOST -p $BCP_DB_PORT -c "SELECT pg_current_xlog_location();" -t | tr -d ' '`
echo "[FAILBACK][MASTER] current xlog location is [$xlog_location]"

ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$BCP_HOST << ENDSSH
    echo "[FAILBACK][BCP] gracefully shutdown bcp database"
    $PGBIN_PATH/pg_ctl stop -m fast -D $BCP_DATA_PATH
ENDSSH

echo "[FAILBACK][MASTER] copy unarchived log files"
rsync -e "ssh -o StrictHostKeyChecking=no" -azhv $DEPLOYMENT_ACCOUNT@$BCP_HOST:$BCP_DATA_PATH/pg_xlog/* $ARCHIVE_PATH

echo "[FAILBACK][MASTER] waiting for master catching up with bcp"
while [ `psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "SELECT pg_xlog_location_diff(pg_last_xlog_replay_location(), '$xlog_location');" -t | tr -d ' '` -lt 0 ]
do
    sleep 1 && echo "[FAILBACK][MASTER] bcp is catching up..."; 
done
echo "[FAILBACK][MASTER] master catch up with bcp!"


echo "[FAILBACK][MASTER] promote master database to take traffic"
touch $PROMOTE_TRIGGER_FILE

echo "[FAILBACK][MASTER] waiting for master promote"
while ! echo exit | psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "SELECT pg_is_in_recovery();" -t | grep "f"; do sleep 1 && echo "[MASTER] master is promoting..."; done
echo "[FAILBACK][MASTER] master promoted!"

echo "[FAILBACK][MASTER] ensure master can be written"
while ! echo exit | psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "insert into dummy_test values($(($(date +%s%N)/1000000)));" || echo "ERROR" | grep -v "ERROR";
do
   sleep 1 && echo "[FAILBACK][MASTER] master is still in read-only status...";
done
echo "[FAILBACK][MASTER] master can be written"

ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$BCP_HOST << ENDSSH
    echo "[BCP] configure recovery.conf for bcp"
    cat > $BCP_DATA_PATH/recovery.conf <<EOF
recovery_target_timeline = 'latest'
restore_command = $RESTORE_COMMAND
standby_mode = 'on'
primary_conninfo = 'user=$PGUSER host=$MASTER_HOST port=$MASTER_DB_PORT sslmode=prefer sslcompression=1 krbsrvname=$PGUSER'
trigger_file = '$PROMOTE_TRIGGER_FILE'
EOF

    echo "[FAILBACK][BCP] start bcp database"
    $PGBIN_PATH/pg_ctl -D $BCP_DATA_PATH -l "${BCP_LOG_PATH}/postgresql-$(date +%Y.%m.%d.%S.%N).log" start > /dev/null 2>&1 &

    while ! echo exit | nc $BCP_HOST $BCP_DB_PORT;
    do
        sleep 1 && echo "[BCP] waiting for bcp database start up...";
    done
    echo "[FAILBACK][BCP] bcp database started successfully!"
ENDSSH

echo "[FAILBACK][MASTER] generate londiste root configuration"
$DEPLOYMENT_PATH/londiste/londiste_config_root.sh

for db in ${REPLICA_DATABASES[@]}
do
    config=$SKYTOOL_CONFIG_PATH/${db}_root.ini

    echo "[FAILBACK][MASTER] update root node location"
    psql ${db} -h $MASTER_HOST -p $MASTER_DB_PORT -c "update pgq_node.node_location set node_location = 'dbname=${db} host=$MASTER_HOST port=$MASTER_DB_PORT' where queue_name = 'queue_${db}' and node_name = 'root_node_${db}';"

    echo "[FAILBACK][MASTER] start londiste worker for database [$db]"
    londiste3 -d $config worker > /dev/null 2>&1 &
done

echo "[FAILBACK][MASTER] start pgqd deamon on master"
$DEPLOYMENT_PATH/londiste/londiste_pgqd.sh

ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$REPLICA_HOST << ENDSSH
    for db in ${REPLICA_DATABASES[@]}
    do
        config=$SKYTOOL_CONFIG_PATH/\${db}_leaf.ini

        echo "[FAILBACK][REPLICA] update root node location"
        psql \${db} -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "update pgq_node.node_location set node_location = 'dbname=\${db} host=$MASTER_HOST port=$MASTER_DB_PORT' where queue_name = 'queue_\${db}' and node_name = 'root_node_\${db}';"

        echo "[FAILBACK][REPLICA] start worker for database [\$db]"
        londiste3 -d \$config worker > /dev/null 2>&1 &
    done

    echo "[FAILBACK][REPLICA] start pgqd deamon"
	$DEPLOYMENT_PATH/londiste/londiste_pgqd.sh
ENDSSH
