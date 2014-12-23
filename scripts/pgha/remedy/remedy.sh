#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

role=`getServerRole`
echo "The server is [$role]"

if [[ $role = "MASTER" ]]; then 
    $DEPLOYMENT_PATH/remedy/remedy_master.sh
    exit 0
fi

if [[ $role = "SLAVE" ]]; then 
    $DEPLOYMENT_PATH/remedy/remedy_slave.sh
    exit 0
fi

if [[ $role = "REPLICA" ]]; then 
    $DEPLOYMENT_PATH/remedy/remedy_replica.sh
    exit 0
fi

if [[ $role = "BCP" ]]; then 
    $DEPLOYMENT_PATH/remedy/remedy_bcp.sh
    exit 0
fi