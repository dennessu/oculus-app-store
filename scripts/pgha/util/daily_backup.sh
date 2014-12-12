#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

echo "[BACKUP][SLAVE] test replication from $MASTER_HOST to $SLAVE_HOST"
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "DELETE FROM tly;"

# insert against master server
echo 'insert 10 records to postgres.tly table on master'
for k in $(seq 1 10)
do
    $PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "INSERT INTO tly (id) VALUES ($k);"
done

export TIMESTAMP=$(($(date +%s%N)/1000000));
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "INSERT INTO tly (id) VALUES ($TIMESTAMP);"

echo 'waiting for replication...'
sleep 5s

echo '[BACKUP][SLAVE] check streaming replication data on SLAVE...'
ANSWER=`$PGBIN_PATH/psql -t -d postgres -h $SLAVE_HOST -p $SLAVE_DB_PORT -c 'SELECT max(id) FROM tly' | sed -e 's/^[ \t]*//'`
echo "timestamp from secondary is $ANSWER"
if [ "$ANSWER" != "$TIMESTAMP" ]; then
    echo "[BACKUP][SLAVE][ERROR] timestamp from secondary is $ANSWER, expected $TIMESTAMP"
    exit 1
fi
ANSWER=`$PGBIN_PATH/psql -t -d postgres -h $SLAVE_HOST -p $SLAVE_DB_PORT -c "SELECT count('x') FROM tly" | sed -e 's/^[ \t]*//'`
echo "[BACKUP][SLAVE] rows found in secondary is $ANSWER"
if [ "$ANSWER" != "11" ]; then
    echo "[BACKUP][SLAVE][ERROR] rows found in secondary is not 11"
    exit 1
fi

echo "[BACKUP][SLAVE] create backup data folder $BACKUP_PATH"
rm -rf $BACKUP_PATH
mkdir $BACKUP_PATH

echo "[BACKUP][SLAVE] start backup slave database..."
pg_basebackup -D $BACKUP_PATH -w -R --xlog-method=stream --dbname="host=$SLAVE_HOST port=$SLAVE_DB_PORT user=$PGUSER"
echo "[BACKUP][SLAVE] finish backup slave database!"
