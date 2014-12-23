#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

# check running on specified server
checkServerRole "SLAVE"

if test -e $PGHA_BASE/disable_auto_failover;
    then echo "[AUTO-FAILOVER][SLAVE] auto failover is disabled"
    exit 1
fi

echo "[AUTO-FAILOVER][SLAVE] start secondary pgbouncer proxy and connect to slave server"
$DEPLOYMENT_PATH/pgbouncer/pgbouncer_slave.sh

ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$MASTER_HOST << ENDSSH
    echo "[AUTO-FAILOVER][MASTER] start primary pgbouncer proxy and connect to slave server"
    $DEPLOYMENT_PATH/pgbouncer/pgbouncer_slave.sh
ENDSSH

