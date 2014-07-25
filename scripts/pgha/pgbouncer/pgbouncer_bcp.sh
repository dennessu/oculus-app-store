#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

echo "[PGBOUNCER] pgbouncer is pointing to bcp db server"
$DEPLOYMENT_PATH/pgbouncer/pgbouncer.sh $BCP_HOST $BCP_DB_PORT