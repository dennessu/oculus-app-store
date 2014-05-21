#!/bin/bash
source common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

echo "kill postgres instance with port [$SLAVE_DB_PORT]..."
forceKill $SLAVE_DB_PORT

echo "kill pgbouncer instance with port [$SECONDARY_PGBOUNCER_PORT]..."
forceKill $SECONDARY_PGBOUNCER_PORT

echo 'remove database data'
rm -rf $SLAVE_DATA_PATH

echo 'remove backup data'
rm -rf $SLAVE_BACKUP_PATH

echo 'remove archive data'
rm -rf $SLAVE_ARCHIVE_PATH

echo 'remove pgbouncer data'
rm -rf $PGBOUNCER_PATH

echo 'purge slave finished!'