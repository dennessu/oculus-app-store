#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#run this test E2E script on master server

echo "do bcp failover"
ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$BCP_HOST << ENDSSH
    $DEPLOYMENT_PATH/switchover/failover_bcp.sh
ENDSSH

echo "test bcp failover"
$DEPLOYMENT_PATH/test/test_bcp2master.sh

echo "e2e bcp failover finished!"