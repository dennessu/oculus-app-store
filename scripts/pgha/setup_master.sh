#!/bin/bash
source common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

echo "kill postgres instance with port [$MASTER_DB_PORT]..."
forceKill $MASTER_DB_PORT

echo "create database data folder $MASTER_DATA_PATH"
rm -rf $MASTER_DATA_PATH
mkdir $MASTER_DATA_PATH
chmod 700 $MASTER_DATA_PATH

echo "create database backup folder $MASTER_BACKUP_PATH"
rm -rf $MASTER_BACKUP_PATH
mkdir $MASTER_BACKUP_PATH

echo "create database archive folder $MASTER_ARCHIVE_PATH"
rm -rf $MASTER_ARCHIVE_PATH
mkdir $MASTER_ARCHIVE_PATH

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
max_prepared_transactions = 100
max_connections = 100
EOF

echo "start master database..."
$PGBIN_PATH/pg_ctl -D $MASTER_DATA_PATH start

while ! echo exit | nc $MASTER_HOST $MASTER_DB_PORT; do sleep 1 && echo "waiting for master database startup..."; done
echo "master database started successfully!"