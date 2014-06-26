#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

echo "[PURGE][MASTER] kill postgres instance with port [$MASTER_DB_PORT]..."
forceKill $MASTER_DB_PORT

echo "[PURGE][MASTER] kill pgbouncer instance with port [$PGBOUNCER_PORT]..."
forceKill $PGBOUNCER_PORT

echo "[PURGE][MASTER] kill skytools instance"
forceKillPid $SKYTOOL_PID_PATH

echo "[PURGE][MASTER] remove database data"
rm -rf $MASTER_DATA_PATH

echo "[PURGE][MASTER] remove backup data"
rm -rf $MASTER_BACKUP_PATH

echo "[PURGE][MASTER] remove archive data"
rm -rf $MASTER_ARCHIVE_PATH

echo "[PURGE][MASTER] remove pgbouncer data"
rm -rf $PGBOUNCER_PATH

echo "[PURGE][MASTER] remove skytools data"
rm -rf $SKYTOOL_PATH

echo "[PURGE][MASTER] purge maseter finished!"