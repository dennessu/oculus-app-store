#!/bin/bash
source common.sh

#check running under 'postgres'
checkAccount $DEPLOYMENT_ACCOUNT

echo "kill postgres instance with port [$MASTER_DB_PORT]..."
forceKill $MASTER_DB_PORT

echo "kill pgbouncer instance with port [$PRIMARY_PGBOUNCER_PORT]..."
forceKill $PRIMARY_PGBOUNCER_PORT

echo 'remove database data'
rm -rf $MASTER_DATA_PATH

echo 'remove backup data'
rm -rf $MASETER_BACKUP_PATH

echo 'remove archive data'
rm -rf $MASTER_ARCHIVE_PATH

echo 'purge finished!'