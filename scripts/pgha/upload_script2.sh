#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/util/common.sh

function uploadMaster {
ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$MASTER_HOST << ENDSSH
sudo rm -rf $DEPLOYMENT_PATH
mkdir $DEPLOYMENT_PATH
ENDSSH

scp -r ./ $DEPLOYMENT_ACCOUNT@$MASTER_HOST:$DEPLOYMENT_PATH

ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$MASTER_HOST << ENDSSH
sudo chown $DEPLOYMENT_ACCOUNT:$DEPLOYMENT_ACCOUNT $DEPLOYMENT_PATH/*
ENDSSH
}

function uploadSlave {
ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$SLAVE_HOST << ENDSSH
sudo rm -rf $DEPLOYMENT_PATH
mkdir $DEPLOYMENT_PATH
ENDSSH

scp -r ./ $DEPLOYMENT_ACCOUNT@$SLAVE_HOST:$DEPLOYMENT_PATH

ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$SLAVE_HOST << ENDSSH
sudo chown $DEPLOYMENT_ACCOUNT:$DEPLOYMENT_ACCOUNT $DEPLOYMENT_PATH/*
ENDSSH
}

function uploadReplica {
ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$REPLICA_HOST << ENDSSH
sudo rm -rf $DEPLOYMENT_PATH
mkdir $DEPLOYMENT_PATH
ENDSSH

scp -r ./ $DEPLOYMENT_ACCOUNT@$REPLICA_HOST:$DEPLOYMENT_PATH

ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$REPLICA_HOST << ENDSSH
sudo chown $DEPLOYMENT_ACCOUNT:$DEPLOYMENT_ACCOUNT $DEPLOYMENT_PATH/*
ENDSSH
}

uploadMaster && echo "upload master done!" &
uploadSlave && echo "upload slave done!" &
uploadReplica && echo "upload replica done!" &
