#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

role=`getServerRole`
echo "The server is [$role]"

host=${role}_HOST
port=${role}_DB_PORT
data_path=${role}_DATA_PATH
log_path=${role}_LOG_PATH

echo "server host [${!host}]"
echo "server port [${!port}]"

echo "[REMEDY][$role] check whether postgresql instance running"
nc -z ${!host} ${!port}

if [[ $? -eq 1 ]] ; then
    echo "[REMEDY][$role] postgresql instance is not running"
    $PGBIN_PATH/pg_ctl -D ${!data_path} -l "${!log_path}/postgresql-$(date +%Y.%m.%d.%S.%N).log" start > /dev/null 2>&1 &

    while ! echo exit | nc ${!host} ${!port};
    do 
        sleep 1 && echo "[REMEDY][$role] waiting for database startup...";
    done
else
    echo "[REMEDY][$role] postgresql instance is running"
fi

psql postgres -h ${!host} -p ${!port} -c "SELECT pg_is_in_recovery();" -t > /dev/null

if [[ ($? = "f") && ($role != "REPLICA") ]] ; then
    echo "[REMEDY][$role] server is taking live traffic"

    echo "[REMEDY][$role] kill skytools instance"
    forceKillPid $SKYTOOL_PID_PATH

    echo "[REMEDY][$role] generate root configuration"
    $DEPLOYMENT_PATH/londiste/londiste_config_root.sh

    for db in ${REPLICA_DATABASES[@]}
    do
        config=$SKYTOOL_CONFIG_PATH/${db}_root.ini

        echo "[REMEDY][$role] start londiste worker for database [$db]"
        londiste3 -d $config worker > /dev/null 2>&1 &
    done

    echo "[REMEDY][$role] start pgqd deamon"
    $DEPLOYMENT_PATH/londiste/londiste_pgqd.sh
else
    echo "the server is standby or replica"
fi

if [[ $role = "REPLICA" ]] ; then
    echo "[REMEDY][$role] kill skytools instance"
    forceKillPid $SKYTOOL_PID_PATH

    echo "[REMEDY][$role] generate leaf configuration"
    $DEPLOYMENT_PATH/londiste/londiste_config_leaf.sh

    for db in ${REPLICA_DATABASES[@]}
    do
        config=$SKYTOOL_CONFIG_PATH/${db}_leaf.ini

        echo "[REMEDY][$role] start worker for database [$db]"
        londiste3 -d $config worker > /dev/null 2>&1 &
    done

    echo "[REMEDY][$role] start pgqd deamon"
    $DEPLOYMENT_PATH/londiste/londiste_pgqd.sh
fi