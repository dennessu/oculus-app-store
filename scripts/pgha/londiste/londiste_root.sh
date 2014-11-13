#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

echo "[LONDISTE] kill skytools instance"
forceKillPid $SKYTOOL_PID_PATH

echo "[LONDISTE] generate root configuration"
$DEPLOYMENT_PATH/londiste/londiste_config_root.sh

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
    $PGBIN_PATH/psql ${db} -h $MASTER_HOST -p $MASTER_DB_PORT -c "\dn" -t | tr -d ' ' | cut -d '|' -f 1 | sed '/^$/d' | while read schema
    do 
	   londiste3 $config remove-table ${schema}.databasechangelog ||  echo "WARN: table [${schema}.databasechangelog] missing"
	   londiste3 $config remove-table ${schema}.databasechangeloglock || echo "WARN: table [${schema}.databasechangelog] missing"
    done
done

echo "[LONDISTE] start pgqd deamon"
$DEPLOYMENT_PATH/londiste/londiste_pgqd.sh