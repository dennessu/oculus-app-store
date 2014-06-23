#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

echo "pgbouncer is pointing to master db server"
$DEPLOYMENT_PATH/pgbouncer/pgbouncer.sh $MASTER_HOST $MASTER_DB_PORT