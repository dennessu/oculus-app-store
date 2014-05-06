#!/bin/bash
source set_env.sh

CURRENT_MASTER_SERVER=$1
CURRENT_MASTER_PORT=$2
CURRENT_MASTER_DATA=$3
CURRENT_MASTER_TRIGGER_FILE=$4

CURRENT_SLAVE_SERVER=$5
CURRENT_SLAVE_PORT=$6
CURRENT_SLAVE_DATA=$7
CURRENT_SLAVE_TRIGGER_FILE=$8

echo "gracefully shutdown current master database..."
$PG_BIN/pg_ctl stop -m fast -D $CURRENT_MASTER_DATA

echo "copy unarchived log files..."
cp -u $CURRENT_MASTER_DATA/pg_xlog/*.* $ARCHIVE_DATA

echo "promote current slave database as new master..."
echo "create trigger file $CURRENT_SLAVE_TRIGGER_FILE"
rm -rf $CURRENT_SLAVE_TRIGGER_FILE
touch $CURRENT_SLAVE_TRIGGER_FILE

echo "configure recovery.conf for old master..."
cat > $CURRENT_MASTER_DATA/recovery.conf <<EOF
recovery_target_timeline = 'latest'
restore_command = 'cp $ARCHIVE_DATA/%f %p'
standby_mode = 'on'
primary_conninfo = 'user=$PG_USER host=$CURRENT_SLAVE_SERVER port=$CURRENT_SLAVE_PORT sslmode=prefer sslcompression=1 krbsrvname=$PG_USER'
trigger_file = '$CURRENT_MASTER_TRIGGER_FILE'
EOF

echo "start old master database..."
$PG_BIN/pg_ctl -D $CURRENT_MASTER_DATA start

while ! echo exit | nc $CURRENT_MASTER_SERVER $CURRENT_MASTER_PORT; do sleep 1 && echo "waiting for old master database..."; done
echo "start old master database successfully!"
