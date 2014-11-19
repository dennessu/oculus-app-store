#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

# check running on specified server
checkServerRole "BCP"

# start BCP database
echo "[REMEDY][BCP] check whether postgresql instance running"

if ! nc -z $BCP_HOST $BCP_DB_PORT ; then
    echo "[REMEDY][BCP] postgresql instance is not running"
    startDB $BCP_DATA_PATH $BCP_LOG_PATH

    while ! echo exit | nc $BCP_HOST $BCP_DB_PORT;
    do 
        sleep 1 && echo "[REMEDY][BCP] waiting for database startup...";
    done
else
    echo "[REMEDY][BCP] postgresql instance is running"
fi

# start pgbouncer
$DEPLOYMENT_PATH/pgbouncer/pgbouncer_bcp.sh





