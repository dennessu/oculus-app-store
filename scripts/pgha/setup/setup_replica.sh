#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

echo "[SETUP][REPLICA] check database existence"
if [ -d $REPLICA_DATA_PATH ];then
    echo "[SETUP][REPLICA] folder $REPLICA_DATA_PATH exists. skip database setup"
    exit 0
fi

echo "[SETUP][REPLICA] create pgha base $PGHA_BASE"
createDir $PGHA_BASE

echo "[SETUP][REPLICA] create database data folder $REPLICA_DATA_PATH"
createDir $REPLICA_DATA_PATH

echo "[SETUP][REPLICA] create database archive folder $ARCHIVE_PATH"
createDir $ARCHIVE_PATH

echo "[SETUP][REPLICA] create database archive folder $REPLICA_LOG_PATH"
createDir $REPLICA_LOG_PATH

echo "[SETUP][REPLCIA] configure REPLCIA role"
echo "REPLICA" > $PGHA_BASE/role.conf

echo "[SETUP][REPLICA] copy backup file from remote master"
rsync -e "ssh -o StrictHostKeyChecking=no" -azhv $DEPLOYMENT_ACCOUNT@$MASTER_HOST:$MASTER_BACKUP_PATH/* $REPLICA_DATA_PATH

echo "[SETUP][REPLICA] configure recovery.conf"
cat > $REPLICA_DATA_PATH/recovery.conf <<EOF
recovery_target_timeline = 'latest'
restore_command = $RESTORE_COMMAND
standby_mode = 'on'
primary_conninfo = 'user=$PGUSER host=$MASTER_HOST port=$MASTER_DB_PORT sslmode=prefer sslcompression=1 krbsrvname=$PGUSER'
trigger_file = '$PROMOTE_TRIGGER_FILE'
EOF

echo "[SETUP][REPLICA] configure pg_hba.conf"
cat > $REPLICA_DATA_PATH/pg_hba.conf <<EOF
# TYPE  DATABASE        USER            ADDRESS                 METHOD

# "local" is for Unix domain socket connections only
local   all             ${PGUSER}                               ident
local   all             ${NEWRELIC_PGUSER}                      ident
local   all             ${ZABBIX_PGUSER}                        ident
# IPv4 local connections:
host    all             ${PGUSER}       127.0.0.1/32            ident
host    all             ${PGUSER}       ${MASTER_HOST}/32       ident
host    all             ${PGUSER}       ${SLAVE_HOST}/32        ident
host    all             ${PGUSER}       ${REPLICA_HOST}/32      ident
host    all             ${READONLY_PGUSER}  127.0.0.1/0         ident
host    all             ${NEWRELIC_PGUSER}  127.0.0.1/0         ident
host    all             ${ZABBIX_PGUSER}  127.0.0.1/0           ident
# IPv6 local connections:
host    all             ${PGUSER}       ::1/128                 ident
# Allow replication connections from localhost, by a user with the
# replication privilege.
host    replication     ${PGUSER}       ${REPLICA_HOST:-127.0.0.1}/32   ident
EOF

echo "[SETUP][REPLICA] configure postgres.conf"
cat >> $REPLICA_DATA_PATH/postgresql.conf <<EOF
archive_command = $ARCHIVE_COMMAND
port = $REPLICA_DB_PORT
EOF

echo "[SETUP][REPLICA] start replica database"
startDB $REPLICA_DATA_PATH $REPLICA_LOG_PATH

while ! echo exit | nc $REPLICA_HOST $REPLICA_DB_PORT;
do
    sleep 1 && echo "[SETUP][REPLICA] waiting for replica database...";
done
echo "[SETUP][REPLICA] replica database started successfully!"

echo "[SETUP][REPLICA] setup and start londiste leaf"
$DEPLOYMENT_PATH/londiste/londiste_leaf.sh

echo "[SETUP][REPLICA] start pgbouncer proxy and connect to replica server"
$DEPLOYMENT_PATH/pgbouncer/pgbouncer_replica.sh

echo "[SETUP][REPLICA] create readonly user"
$DEPLOYMENT_PATH/util/create_user.sh $READONLY_PGUSER $REPLICA_HOST $REPLICA_DB_PORT
