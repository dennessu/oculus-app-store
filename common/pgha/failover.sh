#!/bin/bash
source set_env.sh

echo "current master database: $MASTER_SERVER:$MASTER_PORT"
echo "current slave database: $SLAVE_SERVER:$SLAVE_PORT"

./switch_over.sh $MASTER_SERVER $MASTER_PORT $MASTER_DATA $MASTER_TRIGGER_FILE $SLAVE_SERVER $SLAVE_PORT $SLAVE_DATA $SLAVE_TRIGGER_FILE
