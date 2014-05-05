#!/bin/bash
source set_env.sh

echo "current master database: $SLAVE_SERVER:$SLAVE_PORT"
echo "current slave database: $MASTER_SERVER:$MASTER_PORT"

./switch_over.sh $SLAVE_SERVER $SLAVE_PORT $SLAVE_DATA $SLAVE_TRIGGER_FILE $MASTER_SERVER $MASTER_PORT $MASTER_DATA $MASTER_TRIGGER_FILE
