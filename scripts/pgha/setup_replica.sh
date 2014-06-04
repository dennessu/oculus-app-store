#!/bin/bash
source common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

echo "create database data folder $REPLICA_DATA_PATH"
createDir $REPLICA_DATA_PATH

echo "copy backup file from remote master"
rsync -azhv $DEPLOYMENT_ACCOUNT@$MASTER_HOST:$MASTER_BACKUP_PATH/* $REPLICA_DATA_PATH

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
# Allow replication connections from localhost, by a user with the
# replication privilege.
host    replication     ${PGUSER}       ${MASTER_HOST}/32       ident
host    replication     ${PGUSER}       ${SLAVE_HOST}/32        ident
EOF

echo "configure postgres.conf..."
cat >> $REPLICA_DATA_PATH/postgresql.conf <<EOF
port = $REPLICA_DB_PORT
EOF

echo "start slave database..."
$PGBIN_PATH/pg_ctl -D $SLAVE_DATA_PATH start

while ! echo exit | nc $SLAVE_HOST $SLAVE_DB_PORT; do sleep 1 && echo "waiting for slave database..."; done
echo "slave database started successfully!"