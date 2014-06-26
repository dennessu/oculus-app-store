#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

echo "[PURGE][REPLICA] kill postgres instance with port [$REPLICA_DB_PORT]..."
forceKill $REPLICA_DB_PORT

echo "[PURGE][REPLICA] kill skytools instance"
forceKillPid $SKYTOOL_PID_PATH

echo "[PURGE][REPLICA] remove database data"
rm -rf $REPLICA_DATA_PATH

echo "[PURGE][REPLICA] remove skytools data"
rm -rf $SKYTOOL_PATH

echo "[PURGE][REPLICA] purge replica finished!"