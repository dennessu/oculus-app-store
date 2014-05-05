#!/bin/bash
source set_env.sh
source show_info.sh

echo "stop master databases..."
if (lsof -i:$MASTER_PORT -t)
then
	kill -n 9 $(lsof -i:$MASTER_PORT -t)
else
	echo 'master database is not running...'
fi

echo "stop slave databases..."
if (lsof -i:$SLAVE_PORT -t)
then
	kill -n 9 $(lsof -i:$SLAVE_PORT -t)
else
	echo 'slave database is not running...'
fi

echo "purge master database..."
rm -rf $MASTER_DATA

echo "purge slave database..."
rm -rf $SLAVE_DATA

echo "purge archive data..."
rm -rf $ARCHIVE_DATA

echo "purge backup data..."
rm -rf $BACKUP_DATA

echo "finished!"