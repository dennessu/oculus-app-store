#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

find $ARCHIVE_PATH -name '*' -type f -mtime +3 -delete
gzip $ARCHIVE_PATH/*