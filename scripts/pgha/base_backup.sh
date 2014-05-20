#!/bin/bash
source common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

echo "create backup data folder $BACKUP_PATH"
mkdir -p $BACKUP_PATH
rm -rf $BACKUP_PATH/*

echo "start backup master database..."
pg_basebackup -D $BACKUP_PATH -w -R --xlog-method=stream --dbname="host=$MASTER_HOST port=$MASTER_DB_PORT user=$PGUSER"
echo "finish backup maseter database!"