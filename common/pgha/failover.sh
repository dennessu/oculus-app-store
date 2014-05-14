#!/bin/bash
source set_env.sh
source common.sh

echo "current master database: $MASTER_SERVER:$MASTER_PORT"
echo "current slave database: $SLAVE_SERVER:$SLAVE_PORT"

echo "stop primary pgbouncer proxy..."
forceKill $PRIMARY_PGBOUNCER_PORT

echo "stop secondary pgbouncer proxy..."
forceKill $SECONDARY_PGBOUNCER_PORT

echo "do failover..."
./switch_over.sh $MASTER_SERVER $MASTER_PORT $MASTER_DATA $MASTER_TRIGGER_FILE $SLAVE_SERVER $SLAVE_PORT $SLAVE_DATA $SLAVE_TRIGGER_FILE

echo "switch pgbouncer proxies to new master..."
./pgbouncer_slave.sh