#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#run this test E2E script on master server

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

echo "upgrade table on replica"
$PGBIN_PATH/psql postgres -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "ALTER TABLE gyh ADD COLUMN name varchar(16);"
$PGBIN_PATH/psql postgres -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "ALTER TABLE public2.gyh ADD COLUMN name varchar(16);"
$PGBIN_PATH/psql postgres -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "CREATE TABLE oyzh (id bigint, PRIMARY KEY(id));"
$PGBIN_PATH/psql postgres -h $REPLICA_HOST -p $REPLICA_DB_PORT -c "CREATE TABLE public2.oyzh (id bigint, PRIMARY KEY(id));"

echo "upgrade table on master"
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "ALTER TABLE gyh ADD COLUMN name varchar(16);"
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "ALTER TABLE public2.gyh ADD COLUMN name varchar(16);"
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "CREATE TABLE oyzh (id bigint, PRIMARY KEY(id));"
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "CREATE TABLE public2.oyzh (id bigint, PRIMARY KEY(id));"

echo "upgrade londiste on master"
$DEPLOYMENT_PATH/londiste/londiste_upgrade_root.sh

echo "upgrade londiste on replica"
ssh -o "StrictHostKeyChecking no" $DEPLOYMENT_ACCOUNT@$REPLICA_HOST << ENDSSH
$DEPLOYMENT_PATH/londiste/londiste_upgrade_leaf.sh
ENDSSH

#test against postgres.gyh
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "DELETE FROM gyh;"
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "DELETE FROM public2.gyh;"

echo 'insert 8 records to postgres.gyh table on master'
for k in $(seq 1 8)
do
    $PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "INSERT INTO gyh (id, name) VALUES ($k, 'test_name');"
    $PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "INSERT INTO public2.gyh (id, name) VALUES ($k, 'test_name');"
done

echo 'waiting for replication...'
sleep 5s

echo 'check streaming replication data on SLAVE...'
$PGBIN_PATH/psql postgres -h $SLAVE_HOST -p $SLAVE_DB_PORT -c 'SELECT max(id) as "slave max gyh.id" FROM gyh'
$PGBIN_PATH/psql postgres -h $SLAVE_HOST -p $SLAVE_DB_PORT -c 'SELECT count(1) as "slave gyh count" FROM gyh'
$PGBIN_PATH/psql postgres -h $SLAVE_HOST -p $SLAVE_DB_PORT -c 'SELECT max(id) as "slave max public2.gyh.id" FROM public2.gyh'
$PGBIN_PATH/psql postgres -h $SLAVE_HOST -p $SLAVE_DB_PORT -c 'SELECT count(1) as "slave public2.gyh count" FROM public2.gyh'

echo 'check londiste replication data on REPLICA...'
$PGBIN_PATH/psql postgres -h $REPLICA_HOST -p $REPLICA_DB_PORT -c 'SELECT max(id) as "replica max gyh.id" FROM gyh'
$PGBIN_PATH/psql postgres -h $REPLICA_HOST -p $REPLICA_DB_PORT -c 'SELECT count(1) as "replica gyh count" FROM gyh'
$PGBIN_PATH/psql postgres -h $REPLICA_HOST -p $REPLICA_DB_PORT -c 'SELECT max(id) as "replica max public2.gyh.id" FROM public2.gyh'
$PGBIN_PATH/psql postgres -h $REPLICA_HOST -p $REPLICA_DB_PORT -c 'SELECT count(1) as "replica public2.gyh count" FROM public2.gyh'

#test against postgres.oyzh
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "DELETE FROM oyzh;"
$PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "DELETE FROM public2.oyzh;"

echo 'insert 7 records to postgres.oyzh table on master'
for k in $(seq 1 7)
do
    $PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "INSERT INTO oyzh (id) VALUES ($k);"
    $PGBIN_PATH/psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "INSERT INTO public2.oyzh (id) VALUES ($k);"
done

echo 'waiting for replication...'
sleep 5s

echo 'check streaming replication data on SLAVE...'
$PGBIN_PATH/psql postgres -h $SLAVE_HOST -p $SLAVE_DB_PORT -c 'SELECT max(id) as "slave max oyzh.id" FROM oyzh'
$PGBIN_PATH/psql postgres -h $SLAVE_HOST -p $SLAVE_DB_PORT -c 'SELECT count(1) as "slave oyzh count" FROM oyzh'
$PGBIN_PATH/psql postgres -h $SLAVE_HOST -p $SLAVE_DB_PORT -c 'SELECT max(id) as "slave max public2.oyzh.id" FROM public2.oyzh'
$PGBIN_PATH/psql postgres -h $SLAVE_HOST -p $SLAVE_DB_PORT -c 'SELECT count(1) as "slave public2.oyzh count" FROM public2.oyzh'

echo 'check londiste replication data on REPLICA...'
$PGBIN_PATH/psql postgres -h $REPLICA_HOST -p $REPLICA_DB_PORT -c 'SELECT max(id) as "replica max oyzh.id" FROM oyzh'
$PGBIN_PATH/psql postgres -h $REPLICA_HOST -p $REPLICA_DB_PORT -c 'SELECT count(1) as "replica oyzh count" FROM oyzh'
$PGBIN_PATH/psql postgres -h $REPLICA_HOST -p $REPLICA_DB_PORT -c 'SELECT max(id) as "replica max public2.oyzh.id" FROM public2.oyzh'
$PGBIN_PATH/psql postgres -h $REPLICA_HOST -p $REPLICA_DB_PORT -c 'SELECT count(1) as "replica public2.oyzh count" FROM public2.oyzh'

echo "e2e upgrade finished!"