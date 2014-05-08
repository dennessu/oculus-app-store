#!/bin/bash
source set_env.sh
source common.sh
source pgbouncer.sh

./pgbouncer.sh $MASTER_SERVER $MASTER_PORT

