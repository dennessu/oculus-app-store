#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#run this test E2E script on master server

echo "do failback"
$DEPLOYMENT_PATH/switchover/failback.sh

echo "test failback"
$DEPLOYMENT_PATH/test/test_master2slave.sh

echo "e2e failback finished!"