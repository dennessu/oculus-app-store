#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

echo "pgbouncer is pointing to slave db server"
${DIR}/pgbouncer.sh $SLAVE_HOST $SLAVE_DB_PORT