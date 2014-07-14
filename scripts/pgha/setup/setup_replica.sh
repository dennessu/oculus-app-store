#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

echo "[SETUP][REPLICA] create pgha base $PGHA_BASE"
createDir $PGHA_BASE

echo "[SETUP][REPLICA] create database data folder $REPLICA_DATA_PATH"
createDir $REPLICA_DATA_PATH

echo "[SETUP][REPLICA] create database archive folder $REPLICA_ARCHIVE_PATH"
createDir $REPLICA_ARCHIVE_PATH

echo "[SETUP][REPLICA] create database archive folder $REPLICA_LOG_PATH"
createDir $REPLICA_LOG_PATH

echo "[SETUP][REPLICA] copy backup file from remote master"
rsync -azhv $DEPLOYMENT_ACCOUNT@$MASTER_HOST:$MASTER_BACKUP_PATH/* $REPLICA_DATA_PATH

echo "[SETUP][REPLICA] configure recovery.conf"
cat > $REPLICA_DATA_PATH/recovery.conf <<EOF
recovery_target_timeline = 'latest'
restore_command = 'cp $REPLICA_ARCHIVE_PATH/%f %p'
standby_mode = 'on'
primary_conninfo = 'user=$PGUSER host=$MASTER_HOST port=$MASTER_DB_PORT sslmode=prefer sslcompression=1 krbsrvname=$PGUSER'
trigger_file = '$PROMOTE_TRIGGER_FILE'
EOF

echo "[SETUP][REPLICA] configure pg_hba.conf"
cat > $REPLICA_DATA_PATH/pg_hba.conf <<EOF
# TYPE  DATABASE        USER            ADDRESS                 METHOD

# "local" is for Unix domain socket connections only
local   all             ${PGUSER}                               ident
# IPv4 local connections:
host    all             ${PGUSER}       127.0.0.1/32            ident
host    all             ${PGUSER}       ${MASTER_HOST}/32       ident
host    all             ${PGUSER}       ${SLAVE_HOST}/32        ident
host    all             ${PGUSER}       ${REPLICA_HOST}/32      ident
# IPv6 local connections:
host    all             ${PGUSER}       ::1/128                 ident
EOF

echo "[SETUP][REPLICA] configure postgres.conf"
cat >> $REPLICA_DATA_PATH/postgresql.conf <<EOF
archive_command = 'cp %p $REPLICA_ARCHIVE_PATH/%f'
port = $REPLICA_DB_PORT
EOF

echo "[SETUP][REPLICA] start replica database"
$PGBIN_PATH/pg_ctl -D $REPLICA_DATA_PATH -l "${REPLICA_LOG_PATH}/postgresql-$(date +%Y.%m.%d.%S.%N).log"  start > /dev/null 2>&1 &

while ! echo exit | nc $REPLICA_HOST $REPLICA_DB_PORT;
do
    sleep 1 && echo "[SETUP][REPLICA] waiting for replica database...";
done
echo "[SETUP][REPLICA] replica database started successfully!"

echo "[SETUP][REPLICA] setup and start londiste leaf"
$DEPLOYMENT_PATH/londiste/londiste_leaf.sh

echo "[SETUP][REPLICA] start pgbouncer proxy and connect to replica server"
$DEPLOYMENT_PATH/pgbouncer/pgbouncer_replica.sh