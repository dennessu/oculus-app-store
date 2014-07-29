#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#run this test E2E script on master server

echo "do bcp failback"
$DEPLOYMENT_PATH/switchover/failback_bcp.sh

#primary pgbouncer
$DEPLOYMENT_PATH/pgbouncer/pgbouncer_master.sh

#secondary pgbouncer
ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$SLAVE_HOST << ENDSSH
    $DEPLOYMENT_PATH/pgbouncer/pgbouncer_master.sh
ENDSSH

echo "test bcp failback"
$DEPLOYMENT_PATH/test/test_master2slave.sh

echo "e2e bcp failback finished!"