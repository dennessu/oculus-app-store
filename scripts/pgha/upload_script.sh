#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/util/common.sh

function uploadMaster {
ssh $DEVOPS_ACCOUNT@$MASTER_HOST << ENDSSH
sudo rm -rf $DEPLOYMENT_PATH
mkdir $DEPLOYMENT_PATH
ENDSSH

scp -r ./ $DEVOPS_ACCOUNT@$MASTER_HOST:$DEPLOYMENT_PATH

ssh $DEVOPS_ACCOUNT@$MASTER_HOST << ENDSSH
sudo chown $DEPLOYMENT_ACCOUNT:$DEPLOYMENT_ACCOUNT $DEPLOYMENT_PATH/*
ENDSSH
}

function uploadSlave {
ssh $DEVOPS_ACCOUNT@$SLAVE_HOST << ENDSSH
sudo rm -rf $DEPLOYMENT_PATH
mkdir $DEPLOYMENT_PATH
ENDSSH

scp -r ./ $DEVOPS_ACCOUNT@$SLAVE_HOST:$DEPLOYMENT_PATH

ssh $DEVOPS_ACCOUNT@$SLAVE_HOST << ENDSSH
sudo chown $DEPLOYMENT_ACCOUNT:$DEPLOYMENT_ACCOUNT $DEPLOYMENT_PATH/*
ENDSSH
}

function uploadReplica {
	ssh $DEVOPS_ACCOUNT@$REPLICA_HOST << ENDSSH
sudo rm -rf $DEPLOYMENT_PATH
mkdir $DEPLOYMENT_PATH
ENDSSH

scp -r ./ $DEVOPS_ACCOUNT@$REPLICA_HOST:$DEPLOYMENT_PATH

ssh $DEVOPS_ACCOUNT@$REPLICA_HOST << ENDSSH
sudo chown $DEPLOYMENT_ACCOUNT:$DEPLOYMENT_ACCOUNT $DEPLOYMENT_PATH/*
ENDSSH
}

uploadMaster && echo "upload master done!" &
uploadSlave && echo "upload slave done!" &
uploadReplica && echo "upload replica done!" &
