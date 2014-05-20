#!/bin/bash
source common.sh

echo "secondary pgbouncer is pointing to slave db server"
./pgbouncer.sh $SLAVE_HOST $SLAVE_DB_PORT $SECONDARY_PGBOUNCER_HOST $SECONDARY_PGBOUNCER_PORT