#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

echo "[LONDISTE][REPLICA] generate leaf configuration"
$DEPLOYMENT_PATH/londiste/londiste_config_leaf.sh

for db in ${REPLICA_DATABASES[@]}
do
    config=$SKYTOOL_CONFIG_PATH/${db}_leaf.ini

    echo "[LONDISTE][REPLICA] create leaf node for database [$db]"
    londiste3 $config create-leaf leaf_node_${db} "dbname=$db host=$REPLICA_HOST port=$REPLICA_DB_PORT" --provider="dbname=$db host=$MASTER_HOST port=$MASTER_DB_PORT"

    echo "[LONDISTE][REPLICA] start worker for database [$db]"
    londiste3 -d $config worker > /dev/null 2>&1 &

    echo "[LONDISTE][REPLICA] subscribe all tables"
    londiste3 $config add-table --all --expect-sync

    echo "[LONDISTE][REPLICA] remove liquibase change log tables"
    londiste3 $config remove-table databasechangelog || echo "table missing"
    londiste3 $config remove-table databasechangeloglock || echo "table missing"
done

echo "[LONDISTE][REPLICA] start pgqd deamon"
$DEPLOYMENT_PATH/londiste/londiste_pgqd.sh

