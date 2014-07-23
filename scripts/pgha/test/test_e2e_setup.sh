#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#run this test E2E script on master server

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

echo "setup master..."
$DEPLOYMENT_PATH/setup/setup_master.sh

echo "setup and start londiste root"
$DEPLOYMENT_PATH/londiste/londiste_root.sh

echo "do base backup"
$DEPLOYMENT_PATH/util/base_backup.sh

echo "setup slave..."
ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$SLAVE_HOST << ENDSSH
$DEPLOYMENT_PATH/setup/setup_slave.sh
ENDSSH

echo "setup bcp..."
ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$BCP_HOST << ENDSSH
$DEPLOYMENT_PATH/setup/setup_slave.sh
ENDSSH

echo "setup replica..."
ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$REPLICA_HOST << ENDSSH
$DEPLOYMENT_PATH/setup/setup_replica.sh
ENDSSH

echo "test master->slave streaming repliaction and to master->replica londiste replication"
$DEPLOYMENT_PATH/test/test_master2slave.sh

echo "e2e setup finished!"