#!/bin/bash
source common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

echo "kill postgres instance with port [$REPLICA_DB_PORT]..."
forceKill $MASTER_DB_PORT

echo 'remove database data'
rm -rf $REPLICA_DATA_PATH

echo 'purge replica finished!'