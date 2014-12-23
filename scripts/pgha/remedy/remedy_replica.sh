#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

# check running on specified server
checkServerRole "REPLICA"

# start replica database
echo "[REMEDY][REPLICA] check whether postgresql instance running"

if ! nc -z $REPLICA_HOST $REPLICA_DB_PORT ; then
    echo "[REMEDY][REPLICA] postgresql instance is not running"
    startDB $REPLICA_DATA_PATH $REPLICA_LOG_PATH

    while ! echo exit | nc $REPLICA_HOST $REPLICA_DB_PORT;
    do 
        sleep 1 && echo "[REMEDY][REPLICA] waiting for database startup...";
    done
else
    echo "[REMEDY][REPLICA] postgresql instance is running"
fi

# start londiste leaf
echo "[REMEDY][REPLICA] kill skytools instance"
forceKillPid $SKYTOOL_PID_PATH

echo "[REMEDY][REPLICA] generate leaf configuration"
$DEPLOYMENT_PATH/londiste/londiste_config_leaf.sh

for db in ${REPLICA_DATABASES[@]}
do
    config=$SKYTOOL_CONFIG_PATH/${db}_leaf.ini

    echo "[REMEDY][REPLICA] start worker for database [$db]"
    londiste3 -d $config worker > /dev/null 2>&1 &
done

echo "[REMEDY][REPLICA] start pgqd deamon"
$DEPLOYMENT_PATH/londiste/londiste_pgqd.sh


