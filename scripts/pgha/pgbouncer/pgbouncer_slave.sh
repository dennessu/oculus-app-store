#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

echo "[PGBOUNCER] pgbouncer is pointing to slave db server"
$DEPLOYMENT_PATH/pgbouncer/pgbouncer.sh $SLAVE_HOST $SLAVE_DB_PORT