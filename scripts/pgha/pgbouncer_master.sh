#!/bin/bash
source common.sh

echo "primary pgbouncer is pointing to master db server"
./pgbouncer.sh $MASTER_HOST $MASTER_DB_PORT $PRIMARY_PGBOUNCER_HOST $PRIMARY_PGBOUNCER_PORT