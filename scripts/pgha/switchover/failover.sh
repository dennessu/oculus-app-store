#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

echo "stop traffic for failover..."

echo "stop secondary pgbouncer proxy"
forceKill $PGBOUNCER_PORT

ssh $DEPLOYMENT_ACCOUNT@$MASTER_HOST << ENDSSH
source $DEPLOYMENT_PATH/util/common.sh

echo "stop primary pgbouncer proxy"
forceKill $PGBOUNCER_PORT
ENDSSH

echo "waiting for slave catching up with master..."
while ! echo exit | psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "SELECT 'x' from pg_stat_replication where sent_location != replay_location;" | grep "(0 rows)"; do sleep 1 && echo "slave is catching up..."; done
echo "slave catch up with master!"

echo "waiting for replica catching up with master..."
ssh $DEPLOYMENT_ACCOUNT@$REPLICA_HOST << ENDSSH

ENDSSH

echo "gracefully shutdown master database..."
ssh $DEPLOYMENT_ACCOUNT@$MASTER_HOST << ENDSSH
$PGBIN_PATH/pg_ctl stop -m fast -D $MASTER_DATA_PATH
ENDSSH

echo "copy unarchived log files..."
rsync -azhv $DEPLOYMENT_ACCOUNT@$MASTER_HOST:$MASTER_DATA_PATH/pg_xlog/* $SLAVE_ARCHIVE_PATH

echo "promote slave database to take traffic..."
touch $PROMOTE_TRIGGER_FILE

echo "configure recovery.conf for master..."
ssh $DEPLOYMENT_ACCOUNT@$MASTER_HOST << ENDSSH

cat > $MASTER_DATA_PATH/recovery.conf <<EOF
recovery_target_timeline = 'latest'
restore_command = 'cp $MASTER_ARCHIVE_PATH/%f %p'
standby_mode = 'on'
primary_conninfo = 'user=$PGUSER host=$SLAVE_HOST port=$SLAVE_DB_PORT sslmode=prefer sslcompression=1 krbsrvname=$PGUSER'
trigger_file = '$PROMOTE_TRIGGER_FILE'
EOF

echo "start master database..."
$PGBIN_PATH/pg_ctl -D $MASTER_DATA_PATH start

while ! echo exit | nc $MASTER_HOST $MASTER_DB_PORT; do sleep 1 && echo "waiting for master database..."; done
echo "master database started successfully!"
ENDSSH
