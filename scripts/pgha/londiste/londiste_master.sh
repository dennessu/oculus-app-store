#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

echo "kill skytools instance"
forceKillPid $SKYTOOL_PID_PATH

for db in ${REPLICA_DATABASES[@]}
do
    config=$SKYTOOL_CONFIG_PATH/${db}_root.ini

    echo "create root node for database [$db]"
    londiste3 $config create-root ${db}_root_node "dbname=$db host=$MASTER_HOST port=$MASTER_DB_PORT"

    echo "start worker for database [$db]"
    londiste3 -d $config worker > /dev/null 2>&1 &

    echo "publish all tables"
    londiste3 $config add-table --all
done
