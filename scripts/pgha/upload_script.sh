#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"

ENVIRONMENT=$1
if [[ -z "$ENVIRONMENT" ]]; then 
    echo "usage: $0 <env>"
    exit 1
fi

# note: $DIR changed after source common.sh
source ${DIR}/util/common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

function uploadMaster {

echo Uploading to master $DEPLOYMENT_ACCOUNT@$MASTER_HOST
ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$MASTER_HOST << ENDSSH
    rm -rf $DEPLOYMENT_PATH
    mkdir $DEPLOYMENT_PATH
ENDSSH

scp -r ./ $DEPLOYMENT_ACCOUNT@$MASTER_HOST:$DEPLOYMENT_PATH

ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$MASTER_HOST << ENDSSH
    chown $DEPLOYMENT_ACCOUNT:$DEPLOYMENT_ACCOUNT $DEPLOYMENT_PATH/*
ENDSSH

}

function uploadSlave {

if [[ -z "$SLAVE_HOST" ]]; then
    echo Slave not set for master $MASTER_HOST, skip upload
    return
fi

echo Uploading to slave $DEPLOYMENT_ACCOUNT@$SLAVE_HOST
ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$SLAVE_HOST << ENDSSH
    rm -rf $DEPLOYMENT_PATH
    mkdir $DEPLOYMENT_PATH
ENDSSH

scp -r ./ $DEPLOYMENT_ACCOUNT@$SLAVE_HOST:$DEPLOYMENT_PATH

ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$SLAVE_HOST << ENDSSH
    chown $DEPLOYMENT_ACCOUNT:$DEPLOYMENT_ACCOUNT $DEPLOYMENT_PATH/*
ENDSSH
}

function uploadReplica {

if [[ -z "$REPLICA_HOST" ]]; then
    echo Replica not set for master $MASTER_HOST, skip upload
    return
fi


echo Uploading to replica $DEPLOYMENT_ACCOUNT@$REPLICA_HOST
ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$REPLICA_HOST << ENDSSH
    rm -rf $DEPLOYMENT_PATH
    mkdir $DEPLOYMENT_PATH
ENDSSH

scp -r ./ $DEPLOYMENT_ACCOUNT@$REPLICA_HOST:$DEPLOYMENT_PATH

ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$REPLICA_HOST << ENDSSH
    chown $DEPLOYMENT_ACCOUNT:$DEPLOYMENT_ACCOUNT $DEPLOYMENT_PATH/*
ENDSSH
}

for ENVIRONMENT_FILE in ${DIR}/../env/${ENVIRONMENT}_*.sh
do
    echo "Processing $ENVIRONMENT_FILE"
    source $ENVIRONMENT_FILE

    cp $ENVIRONMENT_FILE ${DIR}/../env/${ENVIRONMENT}.sh

    uploadMaster && echo "upload master done!"
    uploadSlave && echo "upload slave done!"
    uploadReplica && echo "upload replica done!"
done

rm ${DIR}/../env/${ENVIRONMENT}.sh

