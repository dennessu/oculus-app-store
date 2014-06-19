#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

echo "stop primary pgbouncer proxy"
forceKill $PGBOUNCER_PORT

ssh $DEPLOYMENT_ACCOUNT@$SLAVE_HOST << ENDSSH
source $DEPLOYMENT_PATH/util/common.sh

echo "stop secondary pgbouncer proxy"
forceKill $PGBOUNCER_PORT
ENDSSH

echo "waiting for master catching up with slave"
while ! echo exit | psql postgres -h $SLAVE_HOST -p $SLAVE_DB_PORT -c "SELECT 'x' from pg_stat_replication where sent_location != replay_location;" | grep "(0 rows)"; do sleep 1 && echo "master is catching up..."; done
echo "master catch up with slave!"

echo "gracefully shutdown slave database"
ssh $DEPLOYMENT_ACCOUNT@$SLAVE_HOST << ENDSSH
$PGBIN_PATH/pg_ctl stop -m fast -D $SLAVE_DATA_PATH
ENDSSH

echo "copy unarchived log files..."
rsync -azhv $DEPLOYMENT_ACCOUNT@$SLAVE_HOST:$SLAVE_DATA_PATH/pg_xlog/* $MASTER_ARCHIVE_PATH

echo "promote master database to take traffic"
touch $PROMOTE_TRIGGER_FILE

echo "change replica provider "
ssh $DEPLOYMENT_ACCOUNT@$REPLICA_HOST << ENDSSH
londiste3 $config change-provider --provider="dbname=$db host=$MASTER_HOST port=$MASTER_DB_PORT"
ENDSSH

echo "configure recovery.conf for slave"
ssh $DEPLOYMENT_ACCOUNT@$SLAVE_HOST << ENDSSH

cat > $SLAVE_DATA_PATH/recovery.conf <<EOF
recovery_target_timeline = 'latest'
restore_command = 'cp $SLAVE_ARCHIVE_PATH/%f %p'
standby_mode = 'on'
primary_conninfo = 'user=$PGUSER host=$MASTER_HOST port=$MASTER_DB_PORT sslmode=prefer sslcompression=1 krbsrvname=$PGUSER'
trigger_file = '$PROMOTE_TRIGGER_FILE'
EOF

echo "start slave database"
$PGBIN_PATH/pg_ctl -D $SLAVE_DATA_PATH start

while ! echo exit | nc $SLAVE_HOST $SLAVE_DB_PORT; do sleep 1 && echo "waiting for slave database..."; done
echo "slave database started successfully!"
ENDSSH
