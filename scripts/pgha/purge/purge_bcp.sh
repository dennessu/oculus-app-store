#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

echo "[PURGE][BCP] kill postgres instance with port [$BCP_DB_PORT]..."
forceKill $BCP_DB_PORT

echo "[PURGE][BCP] kill pgbouncer instance with port [$PGBOUNCER_PORT]..."
forceKill $PGBOUNCER_PORT

echo "[PURGE][BCP] kill skytools instance"
forceKillPid $SKYTOOL_PID_PATH

echo "[PURGE][BCP] remove database data"
rm -rf $BCP_DATA_PATH

echo "[PURGE][BCP] remove backup data"
rm -rf $BCP_BACKUP_PATH

echo "[PURGE][BCP] remove archive data"
rm -rf $ARCHIVE_PATH

echo "[PURGE][BCP] remove pgbouncer data"
rm -rf $PGBOUNCER_PATH

echo "[PURGE][BCP] remove skytools data"
rm -rf $SKYTOOL_PATH

echo "[PURGE][BCP] purge BCP finished!"