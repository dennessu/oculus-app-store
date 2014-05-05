#!/bin/bash
source set_env.sh

echo "gracefully shutdown master database..."
$PG_BIN/pg_ctl stop -m fast -D $SLAVE_DATA

echo "copy unarchived log files..."
cp -u $SLAVE_DATA/pg_xlog/*.* $ARCHIVE_DATA

echo "promote slave database as new master..."
rm -rf $MASTER_TRIGGER_FILE
touch $MASTER_TRIGGER_FILE

echo "configure recovery.conf for old master..."
cat > $SLAVE_DATA/recovery.conf <<EOF
recovery_target_timeline = 'latest'
restore_command = 'cp $ARCHIVE_DATA/%f %p'
standby_mode = 'on'
primary_conninfo = 'user=$PG_USER host=$MASTER_SERVER port=$MASTER_PORT sslmode=prefer sslcompression=1 krbsrvname=$PG_USER'
trigger_file = '$SLAVE_TRIGGER_FILE'
EOF

echo "start old master database..."
$PG_BIN/pg_ctl -D $SLAVE_DATA start

while ! echo exit | nc $SLAVE_SERVER $SLAVE_PORT; do sleep 1 && echo "waiting for old master database..."; done
echo "start old master database successfully!"
