#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

echo "[SETUP][SLAVE] create pgha base $PGHA_BASE"
createDir $PGHA_BASE

echo "[SETUP][SLAVE] create database data folder $SLAVE_DATA_PATH"
createDir $SLAVE_DATA_PATH

echo "[SETUP][SLAVE] create database backup folder $SLAVE_BACKUP_PATH"
createDir $SLAVE_BACKUP_PATH

echo "[SETUP][SLAVE] create database archive folder $ARCHIVE_PATH"
createDir $ARCHIVE_PATH

echo "[SETUP][SLAVE] create database archive folder $SLAVE_LOG_PATH"
createDir $SLAVE_LOG_PATH

echo "[SETUP][SLAVE] configure SLAVE role"
echo "SLAVE" > $PGHA_BASE/role.conf

echo "[SETUP][SLAVE] copy backup file from remote master"
rsync -e "ssh -o StrictHostKeyChecking=no" -azhv $DEPLOYMENT_ACCOUNT@$MASTER_HOST:$MASTER_BACKUP_PATH/* $SLAVE_DATA_PATH

echo "[SETUP][SLAVE] configure recovery.conf"
cat > $SLAVE_DATA_PATH/recovery.conf <<EOF
recovery_target_timeline = 'latest'
restore_command = $RESTORE_COMMAND
standby_mode = 'on'
primary_conninfo = 'user=$PGUSER host=$MASTER_HOST port=$MASTER_DB_PORT sslmode=prefer sslcompression=1 krbsrvname=$PGUSER'
trigger_file = '$PROMOTE_TRIGGER_FILE'
EOF

echo "[SETUP][SLAVE] configure postgres.conf"
cat >> $SLAVE_DATA_PATH/postgresql.conf <<EOF
archive_command = $ARCHIVE_COMMAND
port = $SLAVE_DB_PORT
EOF

echo "[SETUP][SLAVE] start slave database"
startDB $SLAVE_DATA_PATH

while ! echo exit | nc $SLAVE_HOST $SLAVE_DB_PORT;
do
    sleep 1 && echo "[SETUP][SLAVE] waiting for slave database...";
done
echo "[SETUP][SLAVE] slave database started successfully!"

echo "[SETUP][SLAVE] start secondary pgbouncer proxy and connect to master server"
$DEPLOYMENT_PATH/pgbouncer/pgbouncer_master.sh

