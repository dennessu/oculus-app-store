#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

# check running on specified server
checkServerRole "REPLICA"

echo "[REPAIRE-REPLICA][REPLICA] purge replica"
$DEPLOYMENT_PATH/purge/purge_replica.sh

ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$MASTER_HOST << ENDSSH
    source $DEPLOYMENT_PATH/util/common.sh

    echo "[REPAIRE-REPLICA][MASTER] kill skytools instance"
    forceKillPid $SKYTOOL_PID_PATH

    # purge londiste on master
    echo "[REPAIRE-REPLICA][MASTER] prurge londiste"
    for db in ${REPLICA_DATABASES[@]}
    do
        echo "[REPAIRE-REPLICA][MASTER] drop root node"
        set +e
        psql \${db} -h $MASTER_HOST -p $MASTER_DB_PORT -c "DROP SCHEMA pgq CASCADE;"
        psql \${db} -h $MASTER_HOST -p $MASTER_DB_PORT -c "DROP SCHEMA pgq_ext CASCADE;"
        psql \${db} -h $MASTER_HOST -p $MASTER_DB_PORT -c "DROP SCHEMA pgq_node CASCADE;"
        psql \${db} -h $MASTER_HOST -p $MASTER_DB_PORT -c "DROP SCHEMA londiste CASCADE;"
        set -e
    done

    # do base backup on master
    echo "[REPAIRE-REPLICA][MASTER] kill skytools instances"
    $DEPLOYMENT_PATH/util/base_backup.sh
ENDSSH

echo "[REPAIRE-REPLICA][REPLICA] create pgha base $PGHA_BASE"
createDir $PGHA_BASE

echo "[REPAIRE-REPLICA][REPLICA] create database data folder $REPLICA_DATA_PATH"
createDir $REPLICA_DATA_PATH

echo "[REPAIRE-REPLICA][REPLICA] create database archive folder $ARCHIVE_PATH"
createDir $ARCHIVE_PATH

echo "[REPAIRE-REPLICA][REPLICA] create database archive folder $REPLICA_LOG_PATH"
createDir $REPLICA_LOG_PATH

echo "[REPAIRE-REPLICA][REPLCIA] configure REPLCIA role"
echo "REPLICA" > $PGHA_BASE/role.conf

echo "[REPAIRE-REPLICA][REPLICA] copy backup file from remote master"
rsync -e "ssh -o StrictHostKeyChecking=no" -azhv $DEPLOYMENT_ACCOUNT@$MASTER_HOST:$MASTER_BACKUP_PATH/* $REPLICA_DATA_PATH

echo "[REPAIRE-REPLICA][REPLICA] configure recovery.conf"
cat > $REPLICA_DATA_PATH/recovery.conf <<EOF
recovery_target_timeline = 'latest'
restore_command = $RESTORE_COMMAND
standby_mode = 'on'
primary_conninfo = 'user=$PGUSER host=$MASTER_HOST port=$MASTER_DB_PORT sslmode=prefer sslcompression=1 krbsrvname=$PGUSER'
trigger_file = '$PROMOTE_TRIGGER_FILE'
EOF

echo "[REPAIRE-REPLICA][REPLICA] configure pg_hba.conf"
cat > $REPLICA_DATA_PATH/pg_hba.conf <<EOF
# TYPE  DATABASE        USER            ADDRESS                 METHOD

# "local" is for Unix domain socket connections only
local   all             ${PGUSER}                               ident
local   all             ${NEWRELIC_PGUSER}                      ident
local   all             ${ZABBIX_PGUSER}                        ident
# IPv4 local connections:
host    all             ${PGUSER}       127.0.0.1/32            ident
host    all             ${PGUSER}       ${MASTER_HOST}/32       ident
host    all             ${PGUSER}       ${SLAVE_HOST}/32        ident
host    all             ${PGUSER}       ${REPLICA_HOST}/32      ident
host    all             ${READONLY_PGUSER}  127.0.0.1/0         ident
host    all             ${NEWRELIC_PGUSER}  127.0.0.1/0         ident
host    all             ${ZABBIX_PGUSER}  127.0.0.1/0           ident
# IPv6 local connections:
host    all             ${PGUSER}       ::1/128                 ident
# Allow replication connections from localhost, by a user with the
# replication privilege.
host    replication     ${PGUSER}       ${REPLICA_HOST:-127.0.0.1}/32   ident
EOF

echo "[REPAIRE-REPLICA][REPLICA] configure postgres.conf"
cat >> $REPLICA_DATA_PATH/postgresql.conf <<EOF
archive_command = $ARCHIVE_COMMAND
port = $REPLICA_DB_PORT
EOF

echo "[REPAIRE-REPLICA][REPLICA] start replica database"
startDB $REPLICA_DATA_PATH $REPLICA_LOG_PATH

while ! echo exit | nc $REPLICA_HOST $REPLICA_DB_PORT;
do
    sleep 1 && echo "[REPAIRE-REPLICA][REPLICA] waiting for replica database...";
done
echo "[REPAIRE-REPLICA][REPLICA] replica database started successfully!"

echo "[REPAIRE-REPLICA][REPLICA] setup and start londiste leaf"

echo "[REPAIRE-REPLICA][REPLICA] generate leaf configuration"
$DEPLOYMENT_PATH/londiste/londiste_config_leaf.sh

echo "[REPAIRE-REPLICA][REPLICA] kill skytools instance"
forceKillPid $SKYTOOL_PID_PATH

ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$MASTER_HOST << ENDSSH
    source $DEPLOYMENT_PATH/util/common.sh

    echo "[REPAIRE-REPLICA][MASTER] stop primary pgbouncer proxy"
    forceKill $PGBOUNCER_PORT
ENDSSH

ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$SLAVE_HOST << ENDSSH
    source $DEPLOYMENT_PATH/util/common.sh

    echo "[REPAIRE-REPLICA][SLAVE] stop secondary pgbouncer proxy"
    forceKill $PGBOUNCER_PORT
ENDSSH

xlog_location=`psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "SELECT pg_current_xlog_location();" -t | tr -d ' '`
echo "[REPAIRE-REPLICA][REPLICA] current xlog location is [$xlog_location]"

echo "[REPAIRE-REPLICA][REPLICA] waiting for replica catching up with master"
while [ `psql postgres -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "SELECT pg_xlog_location_diff(pg_last_xlog_replay_location(), '$xlog_location');" -t | tr -d ' '` -lt 0 ]
do
    sleep 1 && echo "[LONDISTE][REPLICA] replica is catching up..."; 
done
echo "[REPAIRE-REPLICA][REPLICA] replica catch up with master!"

echo "[REPAIRE-REPLICA][REPLICA] promote replcia database to cut off streaming replication"
touch $PROMOTE_TRIGGER_FILE

echo "[REPAIRE-REPLICA][REPLICA] waiting for replica promote"
while ! echo exit | psql postgres -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "SELECT pg_is_in_recovery();" -t | grep "f";
do
   sleep 1 && echo "[LONDISTE][REPLICA] replica is promoting...";
done
echo "[REPAIRE-REPLICA][REPLICA] replica promoted!"

echo "[REPAIRE-REPLICA][REPLICA] ensure replica can be written"
while ! echo exit | psql postgres -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "insert into dummy_test values($(($(date +%s%N)/1000000)));" || echo "ERROR" | grep -v "ERROR";
do
   sleep 1 && echo "[LONDISTE][REPLICA] replica is still in read-only status...";
done
echo "[REPAIRE-REPLICA][REPLICA] replica can be written"

ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$MASTER_HOST << ENDSSH
    echo "[REPAIRE-REPLICA][MASTER] stop primary pgbouncer proxy"
    $DEPLOYMENT_PATH/londiste/londiste_root.sh
ENDSSH

for db in ${REPLICA_DATABASES[@]}
do
    config=$SKYTOOL_CONFIG_PATH/${db}_leaf.ini

    echo "[LONDISTE][REPLICA] drop root node"
    set +e
    psql $db -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "DROP SCHEMA pgq CASCADE;"
    psql $db -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "DROP SCHEMA pgq_ext CASCADE;"
    psql $db -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "DROP SCHEMA pgq_node CASCADE;"
    psql $db -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "DROP SCHEMA londiste CASCADE;"
    set -e

    SCHEMA=`$PGBIN_PATH/psql ${db} -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "\dn" -t | tr -d ' ' | cut -d '|' -f 1 | sed '/^$/d'`

    echo "[REPAIRE-REPLICA][REPLICA] drop leaf node if exist"
    londiste3 $config drop-node leaf_node_${db} > /dev/null 2>&1 || echo "node [leaf_node_${db}] does not exist"

ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$MASTER_HOST << ENDSSH
    echo "[REPAIRE-REPLICA][MASTER] drop leaf node if exist"
    londiste3 $SKYTOOL_CONFIG_PATH/${db}_root.ini drop-node leaf_node_${db} > /dev/null 2>&1 || echo "node [leaf_node_${db}] does not exist"
ENDSSH

    echo "[REPAIRE-REPLICA][REPLICA] create leaf node for database [$db]"
    londiste3 $config create-leaf leaf_node_${db} "dbname=$db host=$REPLICA_HOST port=$REPLICA_DB_PORT" --provider="dbname=$db host=$MASTER_HOST port=$MASTER_DB_PORT"

    echo "[REPAIRE-REPLICA][REPLICA] start worker for database [$db]"
    londiste3 -d $config worker > /dev/null 2>&1 &

    echo "[REPAIRE-REPLICA][REPLICA] subscribe all tables"
    londiste3 $config add-table --all --expect-sync

    echo "[REPAIRE-REPLICA][REPLICA] remove liquibase change log tables"
    echo "$SCHEMA" | while read schema
    do 
       londiste3 $config remove-table ${schema}.databasechangelog ||  echo "WARN: table [${schema}.databasechangelog] missing"
       londiste3 $config remove-table ${schema}.databasechangeloglock || echo "WARN: table [${schema}.databasechangelog] missing"
    done
done

echo "[REPAIRE-REPLICA][REPLICA] start pgqd deamon"
$DEPLOYMENT_PATH/londiste/londiste_pgqd.sh

echo "[REPAIRE-REPLICA][REPLICA] start pgbouncer proxy and connect to replica server"
$DEPLOYMENT_PATH/pgbouncer/pgbouncer_replica.sh

ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$MASTER_HOST << ENDSSH
    for db in ${REPLICA_DATABASES[@]}
    do
        config=$SKYTOOL_CONFIG_PATH/\${db}_root.ini

        echo "[REPAIRE-REPLICA][MASTER] start worker for database [\$db]"
        londiste3 -d \$config worker > /dev/null 2>&1 &
    done

    echo "[REPAIRE-REPLICA][MASTER] start pgqd deamon"
    $DEPLOYMENT_PATH/londiste/londiste_pgqd.sh

    echo "[REPAIRE-REPLICA][MASTER] start primary pgbouncer proxy"
    $DEPLOYMENT_PATH/pgbouncer/pgbouncer_master.sh
ENDSSH

ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$SLAVE_HOST << ENDSSH
    echo "[REPAIRE-REPLICA][SLAVE] start secondary pgbouncer proxy"
    $DEPLOYMENT_PATH/pgbouncer/pgbouncer_master.sh
ENDSSH

echo "[REPAIRE-REPLICA][REPLICA] create readonly user"
$DEPLOYMENT_PATH/util/create_user.sh $READONLY_PGUSER $REPLICA_HOST $REPLICA_DB_PORT
