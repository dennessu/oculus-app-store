#!/bin/bash
source common.sh

echo "waiting for slave catching up with master..."
while ! echo exit | psql postgres -h $SLAVE_HOST -p $SLAVE_DB_PORT -c "SELECT 'x' from pg_stat_replication where sent_location != replay_location;" | grep "(0 rows)"; do sleep 1 && echo "slave is catching up..."; done
echo "slave catch up with master!"

echo "stop primary pgbouncer proxy"
forceKill $PGBOUNCER_PORT

ssh $DEPLOYMENT_ACCOUNT@$SLAVE_HOST << ENDSSH
source $DEPLOYMENT_PATH/common.sh

echo "stop secondary pgbouncer proxy"
forceKill $PGBOUNCER_PORT

echo "gracefully shutdown current master database..."
$PGBIN_PATH/pg_ctl stop -m fast -D $SLAVE_DATA_PATH
ENDSSH

echo "copy unarchived log files..."
rsync -azhv $DEPLOYMENT_ACCOUNT@$SLAVE_HOST:$SLAVE_DATA_PATH/pg_xlog/* $MASTER_ARCHIVE_PATH

echo "promote current slave database as new master..."
touch $PROMOTE_TRIGGER_FILE

echo "configure recovery.conf for old master..."
ssh $DEPLOYMENT_ACCOUNT@$SLAVE_HOST << ENDSSH

cat > $SLAVE_DATA_PATH/recovery.conf <<EOF
recovery_target_timeline = 'latest'
restore_command = 'cp $SLAVE_ARCHIVE_PATH/%f %p'
standby_mode = 'on'
primary_conninfo = 'user=$PGUSER host=$MASTER_HOST port=$MASTER_DB_PORT sslmode=prefer sslcompression=1 krbsrvname=$PGUSER'
trigger_file = '$PROMOTE_TRIGGER_FILE'
EOF

echo "start old master database..."
$PGBIN_PATH/pg_ctl -D $SLAVE_DATA_PATH start

while ! echo exit | nc $SLAVE_HOST $SLAVE_DB_PORT; do sleep 1 && echo "waiting for old master database..."; done
echo "old master database started successfully!"
ENDSSH
