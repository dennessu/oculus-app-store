#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#run this test E2E script on master server

echo "do failover"
ssh $DEPLOYMENT_ACCOUNT@$SLAVE_HOST << ENDSSH
    $DEPLOYMENT_PATH/switchover/failover.sh
    $DEPLOYMENT_PATH/pgbouncer/pgbouncer_slave.sh
ENDSSH

$DEPLOYMENT_PATH/pgbouncer/pgbouncer_slave.sh

echo "test failover"
$DEPLOYMENT_PATH/test/test_slave2master.sh

echo "e2e failover finished!"