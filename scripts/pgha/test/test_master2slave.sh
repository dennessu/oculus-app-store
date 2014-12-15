#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "DELETE FROM tly;"
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "DELETE FROM public2.tly;"

# insert against master server
ROWS_TO_INSERT=10
echo "insert $ROWS_TO_INSERT records to tly table on master"
for k in $(seq 1 $ROWS_TO_INSERT)
do
    $PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "INSERT INTO public.tly (id) VALUES ($k);"
    $PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "INSERT INTO public2.tly (id) VALUES ($k);"
done
ROWS_TO_INSERT=$((ROWS_TO_INSERT + 1))

TIMESTAMP=$(($(date +%s%N)/1000000))
echo "insert timestamp to tly $TIMESTAMP"
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "INSERT INTO tly (id) VALUES ($TIMESTAMP);"
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "INSERT INTO public2.tly (id) VALUES ($TIMESTAMP);"

echo 'waiting for replication...'
sleep 5s

HAS_ERROR=0

function checkTable {
    CHECK_HOST=$1
    CHECK_PORT=$2
    CHECK_TABLE=$3
    CHECK_ROLE=$4

    echo 'check replication data on $CHECK_ROLE $CHECK_TABLE...'
    ANSWER=`$PGBIN_PATH/psql -t -d postgres -h $SLAVE_HOST -p $SLAVE_DB_PORT -c "SELECT max(id) FROM $CHECK_TABLE" | sed -e 's/^[ \t]*//'`
    echo "timestamp from $CHECK_ROLE $CHECK_TABLE is $ANSWER"
    if [ "$ANSWER" != "$TIMESTAMP" ]; then
        echo "[TEST][$CHECK_ROLE][ERROR] timestamp from $CHECK_ROLE $CHECK_TABLE is $ANSWER, expected $TIMESTAMP"
        HAS_ERROR=$((HAS_ERROR + 1))
    else
        echo "[TEST][$CHECK_ROLE][SUCCESS] timestamp matches"
    fi
    ANSWER=`$PGBIN_PATH/psql -t -d postgres -h $SLAVE_HOST -p $SLAVE_DB_PORT -c "SELECT count('x') FROM $CHECK_TABLE" | sed -e 's/^[ \t]*//'`
    echo "[TEST][$CHECK_ROLE] rows found in $CHECK_ROLE $CHECK_TABLE is $ANSWER"
    if [ "$ANSWER" != "$ROWS_TO_INSERT" ]; then
        echo "[TEST][$CHECK_ROLE][ERROR] rows found in $CHECK_ROLE $CHECK_TABLE is not $ROWS_TO_INSERT"
        HAS_ERROR=$((HAS_ERROR + 1))
    else
        echo "[TEST][$CHECK_ROLE][SUCCESS] row count matches"
    fi
}

# check slave
if [[ -n "$SLAVE_HOST" ]]; then
    checkTable $SLAVE_HOST $SLAVE_DB_PORT tly SLAVE
    checkTable $SLAVE_HOST $SLAVE_DB_PORT public2.tly SLAVE
fi

# check bcp
if [[ -n "$BCP_HOST" ]]; then
    checkTable $BCP_HOST $BCP_DB_PORT tly BCP
    checkTable $BCP_HOST $BCP_DB_PORT public2.tly BCP
fi

if [[ -n "$REPLICA_HOST" ]]; then
    checkTable $REPLICA_HOST $REPLICA_DB_PORT tly REPLICA
    checkTable $REPLICA_HOST $REPLICA_DB_PORT public2.tly REPLICA
fi

echo
if [[ "$HAS_ERROR" != "0" ]]; then
    echo "Detected $HAS_ERROR errors!"
else
    echo "Replication check is successful!"
fi
exit $HAS_ERROR
