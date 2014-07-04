#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#run this test E2E script on master server

echo "setup master..."
$DEPLOYMENT_PATH/setup/setup_master.sh
$DEPLOYMENT_PATH/pgbouncer/pgbouncer_master.sh
$DEPLOYMENT_PATH/test/test_liquibase_master.sh

$DEPLOYMENT_PATH/londiste/londiste_config_root.sh
$DEPLOYMENT_PATH/londiste/londiste_root.sh
$DEPLOYMENT_PATH/londiste/londiste_pgqd.sh

$DEPLOYMENT_PATH/util/base_backup.sh

echo "setup slave..."
ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$SLAVE_HOST << ENDSSH
$DEPLOYMENT_PATH/setup/setup_slave.sh
$DEPLOYMENT_PATH/pgbouncer/pgbouncer_master.sh
ENDSSH

echo "setup replica..."
ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$REPLICA_HOST << ENDSSH
$DEPLOYMENT_PATH/setup/setup_replica.sh
$DEPLOYMENT_PATH/londiste/londiste_config_leaf.sh
$DEPLOYMENT_PATH/londiste/londiste_leaf.sh
$DEPLOYMENT_PATH/londiste/londiste_pgqd.sh
ENDSSH

echo "test master to slave repliaction"
$DEPLOYMENT_PATH/test/test_master2slave.sh

echo "e2e setup finished!"