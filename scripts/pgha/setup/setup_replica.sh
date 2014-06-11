#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

echo "create database data folder $REPLICA_DATA_PATH"
createDir $REPLICA_DATA_PATH

echo "create database archive folder $REPLICA_ARCHIVE_PATH"
createDir $REPLICA_ARCHIVE_PATH

echo "copy backup file from remote master"
rsync -azhv $DEPLOYMENT_ACCOUNT@$MASTER_HOST:$MASTER_BACKUP_PATH/* $REPLICA_DATA_PATH

echo "configure recovery.conf..."
cat > $SLAVE_DATA_PATH/recovery.conf <<EOF
recovery_target_timeline = 'latest'
restore_command = 'cp $REPLICA_ARCHIVE_PATH/%f %p'
standby_mode = 'on'
primary_conninfo = 'user=$PGUSER host=$MASTER_HOST port=$MASTER_DB_PORT sslmode=prefer sslcompression=1 krbsrvname=$PGUSER'
trigger_file = '$PROMOTE_TRIGGER_FILE'
EOF

echo "configure pg_hba.conf..."
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

echo "configure postgres.conf..."
cat >> $REPLICA_DATA_PATH/postgresql.conf <<EOF
archive_command = 'cp %p $REPLICA_ARCHIVE_PATH/%f'
port = $REPLICA_DB_PORT
EOF

echo "start replica database..."
$PGBIN_PATH/pg_ctl -D $REPLICA_DATA_PATH start

while ! echo exit | nc $REPLICA_HOST $REPLICA_DB_PORT; do sleep 1 && echo "waiting for replica database..."; done
echo "replica database started successfully!"
