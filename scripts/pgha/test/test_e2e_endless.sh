#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

$DEPLOYMENT_PATH/test/test_e2e_purge.sh

$DEPLOYMENT_PATH/test/test_e2e_setup.sh

$DEPLOYMENT_PATH/test/test_e2e_failover.sh

$DEPLOYMENT_PATH/test/test_e2e_failback.sh

$DEPLOYMENT_PATH/test/test_e2e_failover_bcp.sh

$DEPLOYMENT_PATH/test/test_e2e_failback_bcp.sh

while true
do
    $DEPLOYMENT_PATH/test/test_e2e_failover.sh
    $DEPLOYMENT_PATH/test/test_e2e_failback.sh
done
