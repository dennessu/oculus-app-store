#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

echo "[SETUP][MASTER] kill postgres instance with port [$MASTER_DB_PORT]"
forceKill $MASTER_DB_PORT

echo "[SETUP][MASTER] create database data folder $MASTER_DATA_PATH"
createDir $MASTER_DATA_PATH

echo "[SETUP][MASTER] create database backup folder $MASTER_BACKUP_PATH"
createDir $MASTER_BACKUP_PATH 

echo "[SETUP][MASTER] create database archive folder $MASTER_ARCHIVE_PATH"
createDir $MASTER_ARCHIVE_PATH

echo "[SETUP][MASTER] create database log folder $MASTER_LOG_PATH"
createDir $MASTER_LOG_PATH

echo "[SETUP][MASTER] initialize master database"
$PGBIN_PATH/pg_ctl -D $MASTER_DATA_PATH initdb

echo "[SETUP][MASTER] configure pg_hba.conf"
cat > $MASTER_DATA_PATH/pg_hba.conf <<EOF
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
# Allow replication connections from localhost, by a user with the
# replication privilege.
host    replication     ${PGUSER}       ${MASTER_HOST}/32       ident
host    replication     ${PGUSER}       ${SLAVE_HOST}/32        ident
host    replication     ${PGUSER}       ${REPLICA_HOST}/32      ident
EOF

echo "[SETUP][MASTER] configure postgres.conf"
cat >> $MASTER_DATA_PATH/postgresql.conf <<EOF
wal_level = hot_standby
archive_mode = on
max_wal_senders = 3
listen_addresses = '*'
hot_standby = on
max_prepared_transactions = 100
max_connections = 100
archive_command = 'cp %p $MASTER_ARCHIVE_PATH/%f'
port = $MASTER_DB_PORT
EOF

echo "[SETUP][MASTER] start master database"
$PGBIN_PATH/pg_ctl -D $MASTER_DATA_PATH -l "${MASTER_LOG_PATH}/postgresql-$(date +%Y.%m.%d.%S.%N).log" start > /dev/null 2>&1 &

while ! echo exit | nc $MASTER_HOST $MASTER_DB_PORT;
do 
    sleep 1 && echo "[SETUP][MASTER] waiting for master database startup...";
done

echo "[SETUP][MASTER] master database started successfully!"
