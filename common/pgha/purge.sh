#!/bin/bash
source set_env.sh
source show_info.sh
source common.sh

echo "stop master databases..."
forceKill $MASTER_PORT

echo "stop slave databases..."
forceKill $SLAVE_PORT

echo "purge master database..."
rm -rf $MASTER_DATA

echo "purge slave database..."
rm -rf $SLAVE_DATA

echo "purge archive data..."
rm -rf $ARCHIVE_DATA

echo "purge backup data..."
rm -rf $BACKUP_DATA

echo "purge pgbouncer configuration and data..."
rm -rf $PGBOUNCER_BASE

echo "stop primary pgbouncer proxy..."
forceKill $PRIMARY_PGBOUNCER_PORT

echo "stop secondary pgbouncer proxy..."
forceKill $SECONDARY_PGBOUNCER_PORT

echo "finished!"