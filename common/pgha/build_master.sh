#!/bin/bash
source set_env.sh

echo "Clean up exisitng master database..."
rm -rf $MASTER_DATA
rm -rf $ARCHIVE_DATA
kill -n 9 $(lsof -i:$MASTER_PORT -t)

echo "Create master data directory..."
mkdir -p $PG_DATA_BASE
mkdir $ARCHIVE_DATA
mkdir $MASTER_DATA

echo "Initialize master database..."
$PG_BIN/pg_ctl -D $MASTER_DATA initdb

echo "Configure pg_hba.conf..."
cp -f pg_hba.conf.template $MASTER_DATA/pg_hba.conf

echo "Configure postgres.conf..."
cat >> $MASTER_DATA/postgresql.conf <<EOF
wal_level = hot_standby
archive_mode = on
archive_command = 'cp %p $ARCHIVE_DATA/%f'
max_wal_senders = 3
port = $MASTER_PORT
listen_addresses = '*'
EOF

echo "Start master database..."
$PG_BIN/pg_ctl -D $MASTER_DATA start

while ! echo exit | nc $MASTER_SERVER $MASTER_PORT; do sleep 1 && echo "Waiting for master database..."; done
echo "Start master database successfully!"