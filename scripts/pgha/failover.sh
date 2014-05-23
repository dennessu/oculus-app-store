#!/bin/bash
source common.sh

echo "waiting for slave catching up with master..."
while ! echo exit | psql postgres -h $MASTER_HOST -p $MASTER_DB_PORT -c "SELECT 'x' from pg_stat_replication where sent_location != replay_location;" | grep "(0 rows)"; do sleep 1 && echo "slave is catching up..."; done
echo "slave catch up with master!"

echo "stop secondary pgbouncer proxy"
forceKill $PGBOUNCER_PORT

ssh $DEPLOYMENT_ACCOUNT@$MASTER_HOST << ENDSSH
source $DEPLOYMENT_PATH/common.sh

echo "stop primary pgbouncer proxy"
forceKill $PGBOUNCER_PORT

echo "gracefully shutdown current master database..."
$PGBIN_PATH/pg_ctl stop -m fast -D $MASTER_DATA_PATH
ENDSSH

echo "copy unarchived log files..."
rsync -azhv $DEPLOYMENT_ACCOUNT@$MASTER_HOST:$MASTER_DATA_PATH/pg_xlog/* $SLAVE_ARCHIVE_PATH

echo "promote current slave database as new master..."
touch $PROMOTE_TRIGGER_FILE

echo "configure recovery.conf for old master..."
ssh $DEPLOYMENT_ACCOUNT@$MASTER_HOST << ENDSSH

cat > $MASTER_DATA_PATH/recovery.conf <<EOF
recovery_target_timeline = 'latest'
restore_command = 'cp $MASTER_ARCHIVE_PATH/%f %p'
standby_mode = 'on'
primary_conninfo = 'user=$PGUSER host=$SLAVE_HOST port=$SLAVE_DB_PORT sslmode=prefer sslcompression=1 krbsrvname=$PGUSER'
trigger_file = '$PROMOTE_TRIGGER_FILE'
EOF

echo "start old master database..."
$PGBIN_PATH/pg_ctl -D $MASTER_DATA_PATH start

while ! echo exit | nc $MASTER_HOST $MASTER_DB_PORT; do sleep 1 && echo "waiting for old master database..."; done
echo "old master database started successfully!"
ENDSSH
