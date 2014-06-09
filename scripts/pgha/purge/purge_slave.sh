#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

echo "kill postgres instance with port [$SLAVE_DB_PORT]..."
forceKill $SLAVE_DB_PORT

echo "kill pgbouncer instance with port [$PGBOUNCER_PORT]..."
forceKill $PGBOUNCER_PORT

echo "kill skytools instance"
forceKillPid $SKYTOOL_PID_PATH

echo 'remove database data'
rm -rf $SLAVE_DATA_PATH

echo 'remove backup data'
rm -rf $SLAVE_BACKUP_PATH

echo 'remove archive data'
rm -rf $SLAVE_ARCHIVE_PATH

echo 'remove pgbouncer data'
rm -rf $PGBOUNCER_PATH

echo "remove skytools data"
rm -rf $SKYTOOL_PATH

echo 'purge slave finished!'