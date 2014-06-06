#!/bin/bash
source common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

echo "kill postgres instance with port [$REPLICA_DB_PORT]..."
forceKill $MASTER_DB_PORT

echo "kill skytools instance"
forceKillPid $SKYTOOL_PID_PATH

echo 'remove database data'
rm -rf $REPLICA_DATA_PATH

echo "remove skytools data"
rm -rf $SKYTOOL_PATH

echo 'purge replica finished!'