#!/bin/bash
source set_env.sh

# show environment configurtion
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

echo "[PGBouncer Info]"
echo "Primary PGBouncer: $PRIMARY_PGBOUNCER_HOST:$PRIMARY_PGBOUNCER_PORT"
echo "Secondary PGBouncer: $SECONDARY_PGBOUNCER_HOST:$SECONDARY_PGBOUNCER_PORT"
echo ""
