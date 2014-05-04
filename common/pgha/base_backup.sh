#!/bin/bash
source set_env.sh

echo "[Base backup Info]"
echo "Server: $MASTER_SERVER"
echo "Port: $MASTER_PORT"
echo "Backup Data: $BACKUP_DATA"

mkdir -p $PG_DATA_BASE
rm -rf $BACKUP_DATA
mkdir $BACKUP_DATA

echo "Start backup master database..."
pg_basebackup -D $BACKUP_DATA -w -R --xlog-method=stream --dbname="host=$MASTER_SERVER port=$MASTER_PORT user=$PG_USER"
echo "Finish backup master database..."