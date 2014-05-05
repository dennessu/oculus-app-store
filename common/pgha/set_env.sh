#!/bin/bash
export PG_BIN=/Library/PostgreSQL/9.3/bin/
export PG_DATA_BASE=/tmp/pgha_data
export PG_USER=postgres

export MASTER_SERVER=localhost
export MASTER_PORT=5450
export MASTER_DATA=$PG_DATA_BASE/test_master
export MASTER_TRIGGER_FILE=$PG_DATA_BASE/bring_me_up0

export SLAVE_SERVER=localhost
export SLAVE_PORT=5451
export SLAVE_DATA=$PG_DATA_BASE/test_slave
export SLAVE_TRIGGER_FILE=$PG_DATA_BASE/bring_me_up1

export ARCHIVE_DATA=$PG_DATA_BASE/archive
export BACKUP_DATA=$PG_DATA_BASE/backup

echo "[Master Database Info]"
echo "Master Server: $MASTER_SERVER"
echo "Master Port: $MASTER_PORT"
echo "Master Data: $MASTER_DATA"
echo "Master Trigger File: $MASTER_TRIGGER_FILE"
echo ""

echo "[Slave Database Info]"
echo "Slave Server: $SLAVE_SERVER"
echo "Slave Port: $SLAVE_PORT"
echo "Slave Data: $SLAVE_DATA"
echo "Slave Trigger File: $SLAVE_TRIGGER_FILE"
echo ""

echo "[Misc Info]"
echo "Archive Data: $ARCHIVE_DATA"
echo "Backup Data: $BACKUP_DATA"
echo ""