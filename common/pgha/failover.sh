#!/bin/bash
source set_env.sh

echo "Gracefully shutdown master database..."
$PG_BIN/pg_ctl stop -m fast $MASTER_DATA

echo "Copy unarchived log files..."
cp -u $MASTER_DATA/pg_xlog/* $ARCHIVE_DATA

echo "Promote slave database as new master..."
rm -r $SLAVE_TRIGGER_FILE
touch $SLAVE_TRIGGER_FILE

echo "Configure recovery.conf for old master..."
cat > $MASTER_DATA/recovery.conf <<EOF
recovery_target_timeline = 'latest'
restore_command = 'cp $ARCHIVE_DATA/%f %p'
standby_mode = 'on'
primary_conninfo = 'user=$PG_USER host=$SLAVE_SERVER port=$SLAVE_PORT sslmode=prefer sslcompression=1 krbsrvname=$PG_USER'
trigger_file = '$MASTER_TRIGGER_FILE'
EOF

echo "Configure postgres.conf for old master..."
cat >> $MASTER_DATA/postgresql.conf <<EOF
hot_standby = on
EOF

while ! echo exit | nc $MASTER_SERVER $MASTER_PORT; do sleep 1 && echo "Waiting for old master database..."; done
echo "Start old master database successfully!"
