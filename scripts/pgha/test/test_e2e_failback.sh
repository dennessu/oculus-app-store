#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

echo "do failback..."
#$DEPLOYMENT_PATH/switchover/failback.sh
#$DEPLOYMENT_PATH/pgbouncer/pgbouncer_master.sh

#ssh $DEPLOYMENT_ACCOUNT@$SLAVE_HOST << ENDSSH
#$DEPLOYMENT_PATH/pgbouncer/pgbouncer_master.sh
#ENDSSH

#echo "test failback..."
#$DEPLOYMENT_PATH/test/test_master2slave.sh

echo "e2e failover finished!"