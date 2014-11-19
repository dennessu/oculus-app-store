#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

# check running on specified server
checkServerRole "SLAVE"

echo "[UNPLANNED-FAILOVER][SLAVE] promote slave database to take traffic"
touch $PROMOTE_TRIGGER_FILE

echo "[UNPLANNED-FAILOVER][SLAVE] start secondary pgbouncer proxy and connect to slave server"
$DEPLOYMENT_PATH/pgbouncer/pgbouncer_slave.sh

ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$MASTER_HOST << ENDSSH
    echo "[UNPLANNED-FAILOVER][MASTER] start primary pgbouncer proxy and connect to slave server"
    $DEPLOYMENT_PATH/pgbouncer/pgbouncer_slave.sh
ENDSSH

