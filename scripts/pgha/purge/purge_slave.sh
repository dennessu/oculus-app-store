#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

echo "[PURGE][SLAVE] kill postgres instance with port [$SLAVE_DB_PORT]..."
forceKill $SLAVE_DB_PORT

echo "[PURGE][SLAVE] kill pgbouncer instance with port [$PGBOUNCER_PORT]..."
forceKill $PGBOUNCER_PORT

echo "[PURGE][SLAVE] kill skytools instance"
forceKillPid $SKYTOOL_PID_PATH

echo "[PURGE][SLAVE] remove database data"
rm -rf $SLAVE_DATA_PATH

echo "[PURGE][SLAVE] remove backup data"
rm -rf $SLAVE_BACKUP_PATH

echo "[PURGE][SLAVE] remove archive data"
rm -rf $SLAVE_ARCHIVE_PATH

echo "[PURGE][SLAVE] remove pgbouncer data"
rm -rf $PGBOUNCER_PATH

echo "[PURGE][SLAVE] remove skytools data"
rm -rf $SKYTOOL_PATH

echo "[PURGE][SLAVE] purge slave finished!"