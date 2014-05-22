#!/bin/bash
source common.sh

echo "pgbouncer is pointing to slave db server"
./pgbouncer.sh $SLAVE_HOST $SLAVE_DB_PORT