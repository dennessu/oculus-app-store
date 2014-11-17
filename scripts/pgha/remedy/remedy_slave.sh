#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

# check server
role=`getServerRole`
echo "the server is [$role]"

if [[ $role != "SLAVE" ]]; then 
    echo "the server is not [SLAVE], exit"; 
    exit 1
fi

slave_active=1 #slave in active mode
if test -e $SLAVE_DATA_PATH/recovery.conf;
    then slave_active=0 #standby mode
fi

master_active=-1 #master in unknown mode
if ssh -o ConnectTimeout=10 ${DEPLOYMENT_ACCOUNT}@${MASTER_HOST} test -e $MASTER_DATA_PATH/recovery.conf;
    then master_active=0 #standy mode
    else master_active=1 #active mode
fi

function startSlaveDatabase {
    echo "[REMEDY][SLAVE] check whether postgresql instance running"

    if ! nc -z $SLAVE_HOST $SLAVE_DB_PORT ; then
        echo "[REMEDY][SLAVE] postgresql instance is not running"
        startDB $SLAVE_DATA_PATH $SLAVE_LOG_PATH

        while ! echo exit | nc $SLAVE_HOST $SLAVE_DB_PORT;
        do 
            sleep 1 && echo "[REMEDY][SLAVE] waiting for database startup...";
        done
    else
        echo "[REMEDY][SLAVE] postgresql instance is running"
    fi
}

function startLondisteRoot {
    echo "[REMEDY][SLAVE] kill skytools instance"
    forceKillPid $SKYTOOL_PID_PATH

    echo "[REMEDY][SLAVE] generate root configuration"
    $DEPLOYMENT_PATH/londiste/londiste_config_root.sh

    for db in ${REPLICA_DATABASES[@]}
    do
        config=$SKYTOOL_CONFIG_PATH/${db}_root.ini

        echo "[REMEDY][SLAVE] start londiste worker for database [$db]"
        londiste3 -d $config worker > /dev/null 2>&1 &
    done

    echo "[REMEDY][SLAVE] start pgqd deamon"
    $DEPLOYMENT_PATH/londiste/londiste_pgqd.sh
}

if [[ ($slave_active -eq 1) && ($master_active -eq -1) ]] ; then
    echo "slave active & master unknown (master down case)"
    #start slave db
    #start londiste root
    #pgbouncer->slave

    startSlaveDatabase
    startLondisteRoot
    $DEPLOYMENT_PATH/pgbouncer/pgbouncer_slave.sh
    exit 0
fi

if [[ ($slave_active -eq 1) && ($master_active -eq 1) ]] ; then
    echo "slave active & master active (split brain case)"
    #start slave db
    #don't start londiste root
    #pgbouncer->slave

    startSlaveDatabase
    $DEPLOYMENT_PATH/pgbouncer/pgbouncer_slave.sh
    exit 0
fi

if [[ ($slave_active -eq 1) && ($master_active -eq 0) ]] ; then
    echo "slave active & master standby (normal failover case)"
    #start slave db
    #start londiste root
    #pgbouncer->slave

    startSlaveDatabase
    startLondisteRoot
    $DEPLOYMENT_PATH/pgbouncer/pgbouncer_slave.sh
    exit 0
fi

if [[ ($slave_active -eq 0) && ($master_active -eq -1) ]] ; then
    echo "slave standby & master unknown (rare case)"
    #start slave db
    #don't start londiste root
    #don't start pgbouncer

    startSlaveDatabase
    exit 0
fi

if [[ ($slave_active -eq 0) && ($master_active -eq 1) ]] ; then
    echo "slave standby & master active (normal case)"
    #start slave db
    #don't start londiste root
    #pgbouncer->master

    startSlaveDatabase
    $DEPLOYMENT_PATH/pgbouncer/pgbouncer_master.sh
    exit 0
fi

if [[ ($slave_active -eq 0) && ($master_active -eq 0) ]] ; then
    echo "slave standby & master standby (rare case)"
    #start slave db
    #don't start londiste root
    #don't start pgbouncer

    startSlaveDatabase
    exit 0
fi







