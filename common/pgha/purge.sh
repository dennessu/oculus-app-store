#!/bin/bash
source set_env.sh

echo "Stop master databases..."
kill -n 9 $(lsof -i:$MASTER_PORT -t)

echo "Stop slave databases..."
kill -n 9 $(lsof -i:$SLAVE_PORT -t)

echo "Purge master database..."
rm -rf $MASTER_DATA

echo "Purge slave database..."
rm -rf $SLAVE_DATA

echo "Purge archive data..."
rm -rf $ARCHIVE_DATA

echo "Purge backup data..."
rm -rf $BACKUP_DATA

echo "Finished!"