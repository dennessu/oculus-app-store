#!/bin/bash
source common.sh

#check running under 'postgres'
checkAccount $DEPLOYMENT_ACCOUNT

echo "create database data folder $MASTER_DATA_PATH"
mkdir -p $MASTER_DATA_PATH
chmod 700 $MASTER_DATA_PATH

echo "create database backup folder $MASTER_BACKUP_PATH"
mkdir -p $MASTER_BACKUP_PATH

echo "create database archive folder $MASTER_ARCHIVE_PATH"
mkdir -p $MASTER_ARCHIVE_PATH

echo "initialize master database..."
$PGBIN_PATH/pg_ctl -D $MASTER_DATA_PATH initdb

echo "configure pg_hba.conf..."
cp -f pg_hba.conf.template $MASTER_DATA_PATH/pg_hba.conf

echo "configure postgres.conf..."
cat >> $MASTER_DATA_PATH/postgresql.conf <<EOF
wal_level = hot_standby
archive_mode = on
archive_command = 'cp %p $MASTER_ARCHIVE_PATH/%f'
max_wal_senders = 3
port = $MASTER_DB_PORT
listen_addresses = '*'
hot_standby = on
EOF

echo "start master database..."
$PGBIN_PATH/pg_ctl -D $MASTER_DATA_PATH start

while ! echo exit | nc $MASTER_HOST $MASTER_DB_PORT; do sleep 1 && echo "waiting for master database startup..."; done
echo "master database started successfully!"