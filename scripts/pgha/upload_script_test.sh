#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"

export ENVIRONMENT='test'
source ${DIR}/util/common.sh

export DEVOPS_ACCOUNT='ubuntu'

function uploadMaster {
ssh -o "StrictHostKeyChecking no" $DEVOPS_ACCOUNT@$MASTER_HOST << ENDSSH
sudo mkdir -p /etc/silkcloud
sudo touch /etc/silkcloud/configuration.properties
sudo chmod 666 /etc/silkcloud/configuration.properties
sudo echo "envrionment=$ENVIRONMENT" > /etc/silkcloud/configuration.properties

sudo rm -rf $SILKCLOUD_PATH
sudo mkdir -p $DEPLOYMENT_PATH
sudo chown -R $DEVOPS_ACCOUNT:$DEVOPS_ACCOUNT $SILKCLOUD_PATH
ENDSSH

scp -r ./ $DEVOPS_ACCOUNT@$MASTER_HOST:$DEPLOYMENT_PATH

ssh -o "StrictHostKeyChecking no" $DEVOPS_ACCOUNT@$MASTER_HOST << ENDSSH
sudo chown -R $DEPLOYMENT_ACCOUNT:$DEPLOYMENT_ACCOUNT $SILKCLOUD_PATH
ENDSSH
}

function uploadSlave {
ssh -o "StrictHostKeyChecking no" $DEVOPS_ACCOUNT@$SLAVE_HOST << ENDSSH
sudo mkdir -p /etc/silkcloud
sudo touch /etc/silkcloud/configuration.properties
sudo chmod 666 /etc/silkcloud/configuration.properties
sudo echo "envrionment=$ENVIRONMENT" > /etc/silkcloud/configuration.properties

sudo rm -rf $SILKCLOUD_PATH
sudo mkdir -p $DEPLOYMENT_PATH
sudo chown -R $DEVOPS_ACCOUNT:$DEVOPS_ACCOUNT $SILKCLOUD_PATH
ENDSSH

scp -r ./ $DEVOPS_ACCOUNT@$SLAVE_HOST:$DEPLOYMENT_PATH

ssh -o "StrictHostKeyChecking no" $DEVOPS_ACCOUNT@$SLAVE_HOST << ENDSSH
sudo chown -R $DEPLOYMENT_ACCOUNT:$DEPLOYMENT_ACCOUNT $SILKCLOUD_PATH
ENDSSH
}

function uploadReplica {
ssh -o "StrictHostKeyChecking no" $DEVOPS_ACCOUNT@$REPLICA_HOST << ENDSSH
sudo mkdir -p /etc/silkcloud
sudo touch /etc/silkcloud/configuration.properties
sudo chmod 666 /etc/silkcloud/configuration.properties
sudo echo "envrionment=$ENVIRONMENT" > /etc/silkcloud/configuration.properties

sudo rm -rf $SILKCLOUD_PATH
sudo mkdir -p $DEPLOYMENT_PATH
sudo chown -R $DEVOPS_ACCOUNT:$DEVOPS_ACCOUNT $SILKCLOUD_PATH
ENDSSH

scp -r ./ $DEVOPS_ACCOUNT@$REPLICA_HOST:$DEPLOYMENT_PATH

ssh -o "StrictHostKeyChecking no" $DEVOPS_ACCOUNT@$REPLICA_HOST << ENDSSH
sudo chown -R $DEPLOYMENT_ACCOUNT:$DEPLOYMENT_ACCOUNT $SILKCLOUD_PATH
ENDSSH
}

uploadMaster && echo "upload master done!" &
uploadSlave && echo "upload slave done!" &
uploadReplica && echo "upload replica done!" &
