#!/bin/bash
source common.sh

echo "secondary pgbouncer is pointing to master db server"
./pgbouncer.sh $MASTER_HOST $MASTER_DB_PORT $SECONDARY_PGBOUNCER_HOST $SECONDARY_PGBOUNCER_PORT