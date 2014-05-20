#!/bin/bash
source common.sh

echo "primary pgbouncer is pointing to slave db server"
./pgbouncer.sh $SLAVE_HOST $SLAVE_DB_PORT $PRIMARY_PGBOUNCER_HOST $PRIMARY_PGBOUNCER_PORT