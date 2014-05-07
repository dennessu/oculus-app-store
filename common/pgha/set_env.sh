#!/bin/bash

# stop the execution whenever there is an error
set -e

# configure master and slave info
export PG_BIN=/Library/PostgreSQL/9.3/bin/
export PG_DATA_BASE=/tmp/pgha_data
export PG_USER=postgres

export MASTER_SERVER=localhost
export MASTER_PORT=5450
export MASTER_DATA=$PG_DATA_BASE/test_master
export MASTER_TRIGGER_FILE=$PG_DATA_BASE/bring_me_up0

export SLAVE_SERVER=localhost
export SLAVE_PORT=5451
export SLAVE_DATA=$PG_DATA_BASE/test_slave
export SLAVE_TRIGGER_FILE=$PG_DATA_BASE/bring_me_up1

export ARCHIVE_DATA=$PG_DATA_BASE/archive
export BACKUP_DATA=$PG_DATA_BASE/backup

# configure pgbouncer info
export PGBOUNCER_BIN=/usr/local/bin/
export PGBOUNCER_BASE=$PG_DATA_BASE/pgbouncer
export PGBOUNCER_AUTH_FILE=$PGBOUNCER_BASE/userlist.txt

export PRIMARY_PGBOUNCER_HOST=localhost
export PRIMARY_PGBOUNCER_PORT=6450

export SECONDARY_PGBOUNCER_HOST=localhost
export SECONDARY_PGBOUNCER_PORT=6451

export PGBOUNCER_MAX_CONNECTIONS=100
export PGBOUNCER_DEFAULT_POOL_SIZE=100