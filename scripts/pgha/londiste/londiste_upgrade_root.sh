#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

echo "[LONDISTE] generate root configuration"
$DEPLOYMENT_PATH/londiste/londiste_config_root.sh

for db in ${REPLICA_DATABASES[@]}
do
    config=$SKYTOOL_CONFIG_PATH/${db}_root.ini

    echo "[LONDISTE] create root node for database [$db]"
    londiste3 $config create-root root_node_${db} "dbname=$db host=$MASTER_HOST port=$MASTER_DB_PORT"

    echo "[LONDISTE] start worker for database [$db]"
    londiste3 -d $config worker > /dev/null 2>&1 &

    echo "[LONDISTE] add PK for liquibase table temporarily"
    $PGBIN_PATH/psql ${db} -h $MASTER_HOST -p $MASTER_DB_PORT -c "ALTER TABLE databasechangelog ADD PRIMARY KEY (id);" || echo "table missing"
    $PGBIN_PATH/psql ${db} -h $MASTER_HOST -p $MASTER_DB_PORT -c "ALTER TABLE databasechangeloglock ADD PRIMARY KEY (id);" || echo "table missing"

    echo "[LONDISTE] publish all tables"
    londiste3 $config add-table --all

    echo "[LONDISTE] remove PK for liquibase table"
    $PGBIN_PATH/psql ${db} -h $MASTER_HOST -p $MASTER_DB_PORT -c "ALTER TABLE databasechangelog drop constraint databasechangelog_pkey;" || echo "table missing"
    $PGBIN_PATH/psql ${db} -h $MASTER_HOST -p $MASTER_DB_PORT -c "ALTER TABLE databasechangeloglock drop constraint databasechangeloglock_pkey;" || echo "table missing"

    echo "[LONDISTE] remove liquibase change log tables"
	londiste3 $config remove-table databasechangelog ||  echo "table missing"
	londiste3 $config remove-table databasechangeloglock || echo "table missing"
done

echo "[LONDISTE] start pgqd deamon"
$DEPLOYMENT_PATH/londiste/londiste_pgqd.sh