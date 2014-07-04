#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "DELETE FROM student;"

# insert against master server
for k in $(seq 1 100000)
do
    $PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "INSERT INTO student VALUES ($k);"
done
