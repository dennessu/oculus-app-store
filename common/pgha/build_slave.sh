#!/bin/bash
source set_env.sh

echo "Clean up exisitng slave database..."
rm -rf $SLAVE_DATA
kill -n 9 $(lsof -i:$SLAVE_PORT -t)

echo "Create slave data directory..."
mkdir -p $PG_DATA_BASE
mkdir $SLAVE_DATA
chmod 700 $SLAVE_DATA

echo "Copy backup data..."
cp -rf $BACKUP_DATA/* $SLAVE_DATA

echo "Configure recovery.conf..."
cat > $SLAVE_DATA/recovery.conf <<EOF
recovery_target_timeline = 'latest'
restore_command = 'cp $ARCHIVE_DATA/%f %p'
standby_mode = 'on'
primary_conninfo = 'user=$PG_USER host=$MASTER_SERVER port=$MASTER_PORT sslmode=prefer sslcompression=1 krbsrvname=$PG_USER'
trigger_file = '$SLAVE_TRIGGER_FILE'
EOF

echo "Configure postgres.conf..."
cat >> $SLAVE_DATA/postgresql.conf <<EOF
port = $SLAVE_PORT
hot_standby = on
EOF

echo "Start slave database..."
$PG_BIN/pg_ctl -D $SLAVE_DATA start

while ! echo exit | nc $SLAVE_SERVER $SLAVE_PORT; do sleep 1 && echo "Waiting for slave database..."; done
echo "Start slave database successfully!"