#!/bin/bash
source set_env.sh

echo "stop master databases..."
if (lsof -i:$MASTER_PORT -t)
then
	kill -n 9 $(lsof -i:$MASTER_PORT -t)
else
	echo 'master database is not running...'
fi

echo "clean up exisitng master database..."
rm -rf $MASTER_DATA
rm -rf $ARCHIVE_DATA

echo "create master data directory..."
mkdir -p $PG_DATA_BASE
mkdir $ARCHIVE_DATA
mkdir $MASTER_DATA

echo "initialize master database..."
$PG_BIN/pg_ctl -D $MASTER_DATA initdb

echo "configure pg_hba.conf..."
cp -f pg_hba.conf.template $MASTER_DATA/pg_hba.conf

echo "configure postgres.conf..."
cat >> $MASTER_DATA/postgresql.conf <<EOF
wal_level = hot_standby
archive_mode = on
archive_command = 'cp %p $ARCHIVE_DATA/%f'
max_wal_senders = 3
port = $MASTER_PORT
listen_addresses = '*'
hot_standby = on
EOF

echo "start master database..."
$PG_BIN/pg_ctl -D $MASTER_DATA start

while ! echo exit | nc $MASTER_SERVER $MASTER_PORT; do sleep 1 && echo "Waiting for master database..."; done
echo "start master database successfully!"