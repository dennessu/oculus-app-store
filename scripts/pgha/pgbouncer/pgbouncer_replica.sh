#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

echo "[PGBOUNCER] pgbouncer is pointing to replica db server"
$DEPLOYMENT_PATH/pgbouncer/pgbouncer.sh $REPLICA_HOST $REPLICA_DB_PORT