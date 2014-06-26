#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

echo "[LONDISTE] kill skytools instance"
forceKillPid $SKYTOOL_PID_PATH

for db in ${REPLICA_DATABASES[@]}
do
    config=$SKYTOOL_CONFIG_PATH/${db}_root.ini

    echo "[LONDISTE] drop root node if exist"
    londiste3 $config drop-node root_node_${db} > /dev/null 2>&1 || echo "[LONDISTE] node [root_node_${db}] does not exist"

    echo "[LONDISTE] create root node for database [$db]"
    londiste3 $config create-root root_node_${db} "dbname=$db host=$MASTER_HOST port=$MASTER_DB_PORT"

    echo "[LONDISTE] start worker for database [$db]"
    londiste3 -d $config worker > /dev/null 2>&1 &

    echo "[LONDISTE] publish all tables"
    londiste3 $config add-table --all

    echo "[LONDISTE] remove liquibase change log tables"
	londiste3 $config remove-table databasechangelog ||  echo "table missing"
	londiste3 $config remove-table databasechangeloglock || echo "table missing"
done
