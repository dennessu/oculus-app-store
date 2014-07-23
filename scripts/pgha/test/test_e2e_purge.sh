#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#run this test E2E script on master server

echo "purge slave..."
ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$SLAVE_HOST << ENDSSH
$DEPLOYMENT_PATH/purge/purge_slave.sh
ENDSSH

echo "purge bcp..."
ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$BCP_HOST << ENDSSH
$DEPLOYMENT_PATH/purge/purge_slave.sh
ENDSSH

echo "purge replica..."
ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$REPLICA_HOST << ENDSSH
$DEPLOYMENT_PATH/purge/purge_replica.sh
ENDSSH

echo "setup master..."
$DEPLOYMENT_PATH/purge/purge_master.sh