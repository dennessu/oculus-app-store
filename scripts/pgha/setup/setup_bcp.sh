#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

echo "[SETUP][BCP] create pgha base $PGHA_BASE"
createDir $PGHA_BASE

echo "[SETUP][BCP] create database data folder $BCP_DATA_PATH"
createDir $BCP_DATA_PATH

echo "[SETUP][BCP] create database backup folder $BCP_BACKUP_PATH"
createDir $BCP_BACKUP_PATH

echo "[SETUP][BCP] create database archive folder $BCP_ARCHIVE_PATH"
createDir $BCP_ARCHIVE_PATH

echo "[SETUP][BCP] create database archive folder $BCP_LOG_PATH"
createDir $BCP_LOG_PATH

echo "[SETUP][BCP] configure BCP role"
echo "BCP" > $PGHA_BASE/role.conf

echo "[SETUP][BCP] copy backup file from remote master"
rsync -azhv $DEPLOYMENT_ACCOUNT@$MASTER_HOST:$MASTER_BACKUP_PATH/* $BCP_DATA_PATH

echo "[SETUP][BCP] configure recovery.conf"
cat > $BCP_DATA_PATH/recovery.conf <<EOF
recovery_target_timeline = 'latest'
restore_command = 'cp $BCP_ARCHIVE_PATH/%f %p'
standby_mode = 'on'
primary_conninfo = 'user=$PGUSER host=$MASTER_HOST port=$MASTER_DB_PORT sslmode=prefer sslcompression=1 krbsrvname=$PGUSER'
trigger_file = '$PROMOTE_TRIGGER_FILE'
EOF

echo "[SETUP][BCP] configure postgres.conf"
cat >> $BCP_DATA_PATH/postgresql.conf <<EOF
archive_command = 'cp %p $BCP_ARCHIVE_PATH/%f'
port = $BCP_DB_PORT
EOF

echo "[SETUP][BCP] start BCP database"
$PGBIN_PATH/pg_ctl -D $BCP_DATA_PATH -l "${BCP_LOG_PATH}/postgresql-$(date +%Y.%m.%d.%S.%N).log" start > /dev/null 2>&1 &

while ! echo exit | nc $BCP_HOST $BCP_DB_PORT;
do
    sleep 1 && echo "[SETUP][BCP] waiting for BCP database...";
done
echo "[SETUP][BCP] BCP database started successfully!"
