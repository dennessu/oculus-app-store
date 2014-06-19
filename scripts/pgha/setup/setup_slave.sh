#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

echo "create database data folder $SLAVE_DATA_PATH"
createDir $SLAVE_DATA_PATH

echo "create database backup folder $SLAVE_BACKUP_PATH"
createDir $SLAVE_BACKUP_PATH

echo "create database archive folder $SLAVE_ARCHIVE_PATH"
createDir $SLAVE_ARCHIVE_PATH

echo "copy backup file from remote master"
rsync -azhv $DEPLOYMENT_ACCOUNT@$MASTER_HOST:$MASTER_BACKUP_PATH/* $SLAVE_DATA_PATH

echo "configure recovery.conf"
cat > $SLAVE_DATA_PATH/recovery.conf <<EOF
recovery_target_timeline = 'latest'
restore_command = 'cp $SLAVE_ARCHIVE_PATH/%f %p'
standby_mode = 'on'
primary_conninfo = 'user=$PGUSER host=$MASTER_HOST port=$MASTER_DB_PORT sslmode=prefer sslcompression=1 krbsrvname=$PGUSER'
trigger_file = '$PROMOTE_TRIGGER_FILE'
EOF

echo "configure pg_hba.conf"
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

echo "configure postgres.conf"
cat >> $SLAVE_DATA_PATH/postgresql.conf <<EOF
archive_command = 'cp %p $SLAVE_ARCHIVE_PATH/%f'
port = $SLAVE_DB_PORT
EOF

echo "start slave database"
$PGBIN_PATH/pg_ctl -D $SLAVE_DATA_PATH start > /dev/null 2>&1 &

while ! echo exit | nc $SLAVE_HOST $SLAVE_DB_PORT; do sleep 1 && echo "waiting for slave database..."; done
echo "slave database started successfully!"