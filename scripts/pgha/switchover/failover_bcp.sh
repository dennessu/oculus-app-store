#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

role=`getServerRole`
echo "The server is [$role]"

if [[ $role != "BCP" ]] ; then
    echo "[REMEDY][$role] bcp failover script should be executed on [BCP] server"
    exit
fi

echo "[FAILOVER][BCP] stop traffic for failover"

echo "[FAILOVER][BCP] stop secondary pgbouncer proxy"
forceKill $PGBOUNCER_PORT

ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$MASTER_HOST << ENDSSH
    source $DEPLOYMENT_PATH/util/common.sh

    echo "[FAILOVER][MASTER] stop primary pgbouncer proxy"
    forceKill $PGBOUNCER_PORT

    echo "[FAILOVER][MASTER] kill skytools instances"
    forceKillPid $SKYTOOL_PID_PATH
ENDSSH

ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$REPLICA_HOST << ENDSSH
    source $DEPLOYMENT_PATH/util/common.sh

    echo "[FAILOVER][REPLICA] kill skytools instances"
    forceKillPid $SKYTOOL_PID_PATH
ENDSSH

xlog_location=`psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "SELECT pg_current_xlog_location();" -t | tr -d ' '`
echo "[FAILOVER][MASTER] current xlog location is [$xlog_location]"

ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$MASTER_HOST << ENDSSH
    source $DEPLOYMENT_PATH/util/common.sh

    echo "[FAILOVER][MASTER] gracefully shutdown master database"
    stopDB $MASTER_DATA_PATH
ENDSSH

echo "[FAILOVER][BCP] copy unarchived log files"
rsync -e "ssh -o StrictHostKeyChecking=no" -azhv $DEPLOYMENT_ACCOUNT@$MASTER_HOST:$MASTER_DATA_PATH/pg_xlog/* $ARCHIVE_PATH

echo "[FAILOVER][BCP] waiting for bcp catching up with master"
while [ `psql postgres -h $BCP_HOST -p $BCP_DB_PORT -c "SELECT pg_xlog_location_diff(pg_last_xlog_replay_location(), '$xlog_location');" -t | tr -d ' '` -lt 0 ]
do
    sleep 1 && echo "[BCP] bcp is catching up..."; 
done
echo "[FAILOVER][BCP] bcp catch up with master!"

echo "[FAILOVER][BCP] promote bcp database to take traffic"
touch $PROMOTE_TRIGGER_FILE

echo "[FAILOVER][BCP] waiting for bcp promote"
while ! echo exit | psql postgres -h $BCP_HOST -p $BCP_DB_PORT -c "SELECT pg_is_in_recovery();" -t | grep "f"; do sleep 1 && echo "[BCP] bcp is promoting..."; done
echo "[FAILOVER][BCP] bcp promoted!"

echo "[FAILOVER][BCP] ensure bcp can be written"
while ! echo exit | psql postgres -h $BCP_HOST -p $BCP_DB_PORT -c "insert into dummy_test values($(($(date +%s%N)/1000000)));" || echo "ERROR" | grep -v "ERROR";
do
   sleep 1 && echo "[FAILOVER][BCP] bcp is still in read-only status...";
done
echo "[FAILOVER][BCP] bcp can be written"

ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$MASTER_HOST << ENDSSH
    source $DEPLOYMENT_PATH/util/common.sh

    echo "[FAILOVER][MASTER] configure recovery.conf for master"
    cat > $MASTER_DATA_PATH/recovery.conf <<EOF
recovery_target_timeline = 'latest'
restore_command = $RESTORE_COMMAND
standby_mode = 'on'
primary_conninfo = 'user=$PGUSER host=$BCP_HOST port=$BCP_DB_PORT sslmode=prefer sslcompression=1 krbsrvname=$PGUSER'
trigger_file = '$PROMOTE_TRIGGER_FILE'
EOF

    echo "[FAILOVER][MASTER] start master database"
    startDB $MASTER_DATA_PATH $MASTER_LOG_PATH

    while ! echo exit | nc $MASTER_HOST $MASTER_DB_PORT;
    do
        sleep 1 && echo "[FAILOVER][MASTER] waiting for master database start up...";
    done
    echo "[FAILOVER][MASTER] master database started successfully!"
ENDSSH

echo "[BCP] generate londiste root configuration"
$DEPLOYMENT_PATH/londiste/londiste_config_root.sh

for db in ${REPLICA_DATABASES[@]}
do
    config=$SKYTOOL_CONFIG_PATH/${db}_root.ini

    echo "[FAILOVER][BCP] update root node location"
    psql ${db} -h $BCP_HOST -p $BCP_DB_PORT -c "update pgq_node.node_location set node_location = 'dbname=${db} host=$BCP_HOST port=$BCP_DB_PORT' where queue_name = 'queue_${db}' and node_name = 'root_node_${db}';"

    echo "[FAILOVER][BCP] start londiste worker for database [$db]"
    londiste3 -d $config worker > /dev/null 2>&1 &
done

echo "[FAILOVER][BCP] start pgqd deamon on bcp"
$DEPLOYMENT_PATH/londiste/londiste_pgqd.sh

ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$REPLICA_HOST << ENDSSH
    for db in ${REPLICA_DATABASES[@]}
    do
        config=$SKYTOOL_CONFIG_PATH/\${db}_leaf.ini

        echo "[FAILOVER][REPLICA] update root node location"
        psql \${db} -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "update pgq_node.node_location set node_location = 'dbname=\${db} host=$BCP_HOST port=$BCP_DB_PORT' where queue_name = 'queue_\${db}' and node_name = 'root_node_\${db}';"

        echo "[FAILOVER][REPLICA] start worker for database [\$db]"
        londiste3 -d \$config worker > /dev/null 2>&1 &
    done

    echo "[FAILOVER][REPLICA] start pgqd deamon"
	$DEPLOYMENT_PATH/londiste/londiste_pgqd.sh
ENDSSH

echo "[FAILOVER][BCP] point pgbouncer to bcp"
ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$MASTER_HOST << ENDSSH
    $DEPLOYMENT_PATH/pgbouncer/pgbouncer_bcp.sh
ENDSSH

ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$SLAVE_HOST << ENDSSH
    $DEPLOYMENT_PATH/pgbouncer/pgbouncer_bcp.sh
ENDSSH
