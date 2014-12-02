#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

# stop postgresql instance
stopDB $DATA_PATH $DB_LOG_PATH

# restart all service
$DEPLOYMENT_PATH/remedy/remedy.sh