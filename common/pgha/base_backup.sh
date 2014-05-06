#!/bin/bash
source set_env.sh
source show_info.sh

mkdir -p $PG_DATA_BASE
rm -rf $BACKUP_DATA
mkdir $BACKUP_DATA

echo "start backup master database..."
pg_basebackup -D $BACKUP_DATA -w -R --xlog-method=stream --dbname="host=$MASTER_SERVER port=$MASTER_PORT user=$PG_USER"
echo "finish backup master database..."