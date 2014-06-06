#!/bin/bash
source common.sh

echo "pgbouncer is pointing to master db server"
./pgbouncer.sh $MASTER_HOST $MASTER_DB_PORT