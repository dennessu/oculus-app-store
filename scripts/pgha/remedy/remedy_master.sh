#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

# check server
role=`getServerRole`
echo "the server is [$role]"

if [[ $role != "MASTER" ]]; then 
    echo "the server is not [MASTER], exit"; 
    exit 1
fi

master_active=1 #master in active mode
if test -e $MASTER_DATA_PATH/recovery.conf;
    then master_active=0 #standby mode
fi

slave_active=-1 #slave in unknown mode
if ssh -o ConnectTimeout=10 ${DEPLOYMENT_ACCOUNT}@${SLAVE_HOST} test -e $SLAVE_DATA_PATH/recovery.conf;
    then slave_active=0 #standy mode
    else slave_active=1 #active mode
fi

function startMasterDatabase {
    echo "[REMEDY][MASTER] check whether postgresql instance running"

    if ! nc -z $MASTER_HOST $MASTER_DB_PORT ; then
        echo "[REMEDY][MASTER] postgresql instance is not running"
        startDB $MASTER_DATA_PATH $MASTER_LOG_PATH

        while ! echo exit | nc $MASTER_HOST $MASTER_DB_PORT;
        do 
            sleep 1 && echo "[REMEDY][MASTER] waiting for database startup...";
        done
    else
        echo "[REMEDY][MASTER] postgresql instance is running"
    fi
}

function startLondisteRoot {
    echo "[REMEDY][MASTER] kill skytools instance"
    forceKillPid $SKYTOOL_PID_PATH

    echo "[REMEDY][MASTER] generate root configuration"
    $DEPLOYMENT_PATH/londiste/londiste_config_root.sh

    for db in ${REPLICA_DATABASES[@]}
    do
        config=$SKYTOOL_CONFIG_PATH/${db}_root.ini

        echo "[REMEDY][MASTER] start londiste worker for database [$db]"
        londiste3 -d $config worker > /dev/null 2>&1 &
    done

    echo "[REMEDY][MASTER] start pgqd deamon"
    $DEPLOYMENT_PATH/londiste/londiste_pgqd.sh
}

if [[ ($master_active -eq 1) && ($slave_active -eq -1) ]] ; then
    echo "master active & slave unknown (slave down case)"
    #start master db
    #start londiste root
    #pgbouncer->master

    startMasterDatabase
    startLondisteRoot
    $DEPLOYMENT_PATH/pgbouncer/pgbouncer_master.sh
    exit 0
fi

if [[ ($master_active -eq 1) && ($slave_active -eq 1) ]] ; then
    echo "master active & slave active (split brain case)"
    #don't start master db
    #don't start londiste root
    #pgbouncer->slave

    $DEPLOYMENT_PATH/pgbouncer/pgbouncer_slave.sh
    exit 0
fi

if [[ ($master_active -eq 1) && ($slave_active -eq 0) ]] ; then
    echo "master active & slave standby (normal case)"
    #start master db
    #start londiste root
    #pgbouncer->master

    startMasterDatabase
    startLondisteRoot
    $DEPLOYMENT_PATH/pgbouncer/pgbouncer_master.sh
    exit 0
fi

if [[ ($master_active -eq 0) && ($slave_active -eq -1) ]] ; then
    echo "master standby & slave unknown (rare case)"
    #start master db
    #don't start londiste root
    #dont't start pgbouncer

    startMasterDatabase
    exit 0
fi

if [[ ($master_active -eq 0) && ($slave_active -eq 1) ]] ; then
    echo "master standby & slave active (normal failover case)"
    #start master db
    #don't start londiste root
    #pgbouncer->slave

    startMasterDatabase
    $DEPLOYMENT_PATH/pgbouncer/pgbouncer_slave.sh
    exit 0
fi

if [[ ($master_active -eq 0) && ($slave_active -eq 0) ]] ; then
    echo "master standby & slave standby (rare case)"
    #start master db
    #don't start londiste root
    #don't start pgbouncer

    startMasterDatabase
    exit 0
fi







