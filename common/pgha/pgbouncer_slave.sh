#!/bin/bash
source set_env.sh
source common.sh
source pgbouncer.sh

./pgbouncer.sh $SLAVE_SERVER $SLAVE_PORT

