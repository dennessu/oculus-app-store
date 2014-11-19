#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

# check running on specified server
checkServerRole "MASTER"

ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$SLAVE_HOST << ENDSSH
    # do base backup on slave
    rm -rf $BACKUP_PATH
    mkdir $BACKUP_PATH
    pg_basebackup -D $BACKUP_PATH -w -R --xlog-method=stream --dbname="host=$SLAVE_HOST port=$SLAVE_DB_PORT user=$PGUSER"
ENDSSH

echo "[REPAIR-MASTER][MASTER] create pgha base $PGHA_BASE"
createDir $PGHA_BASE

echo "[REPAIR-MASTER][MASTER] create database data folder $MASTER_DATA_PATH"
createDir $MASTER_DATA_PATH

echo "[REPAIR-MASTER][MASTER] create database backup folder $MASTER_BACKUP_PATH"
createDir $MASTER_BACKUP_PATH

echo "[REPAIR-MASTER][MASTER] create database archive folder $ARCHIVE_PATH"
createDir $ARCHIVE_PATH

echo "[REPAIR-MASTER][MASTER] create database archive folder $MASTER_LOG_PATH"
createDir $MASTER_LOG_PATH

echo "[REPAIR-MASTER][MASTER] configure MASTER role"
echo "MASTER" > $PGHA_BASE/role.conf

echo "[REPAIR-MASTER][MASTER] copy backup file from remote master"
rsync -e "ssh -o StrictHostKeyChecking=no" -azhv $DEPLOYMENT_ACCOUNT@$SLAVE_HOST:$SLAVE_BACKUP_PATH/* $MASTER_DATA_PATH

echo "[REPAIR-MASTER][MASTER] configure recovery.conf"
cat > $MASTER_DATA_PATH/recovery.conf <<EOF
recovery_target_timeline = 'latest'
restore_command = $RESTORE_COMMAND
standby_mode = 'on'
primary_conninfo = 'user=$PGUSER host=$SLAVE_HOST port=$SLAVE_DB_PORT sslmode=prefer sslcompression=1 krbsrvname=$PGUSER'
trigger_file = '$PROMOTE_TRIGGER_FILE'
EOF

echo "[REPAIR-MASTER][MASTER] configure postgres.conf"
cat >> $MASTER_DATA_PATH/postgresql.conf <<EOF
archive_command = $ARCHIVE_COMMAND
port = $MASTER_DB_PORT
EOF

echo "[REPAIR-MASTER][MASTER] start master database"
startDB $MASTER_DATA_PATH $MASTER_LOG_PATH

while ! echo exit | nc $MASTER_HOST $MASTER_DB_PORT;
do
    sleep 1 && echo "[REPAIR-MASTER] waiting for master database...";
done
echo "[REPAIR-MASTER][MASTER] master database started successfully!"

echo "[REPAIR-MASTER][MASTER] start secondary pgbouncer proxy and connect to slave server"
$DEPLOYMENT_PATH/pgbouncer/pgbouncer_slave.sh

