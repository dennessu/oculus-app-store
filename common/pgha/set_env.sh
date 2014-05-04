#!/bin/bash
export PG_BIN=/Library/PostgreSQL/9.3/bin/
export PG_DATA_BASE=/tmp/pgha_data
export PG_USER=postgres

export MASTER_SERVER=localhost
export MASTER_PORT=5450
export MASTER_DATA=$PG_DATA_BASE/test_master

export SLAVE_SERVER=localhost
export SLAVE_PORT=5451
export SLAVE_DATA=$PG_DATA_BASE/test_slave

export ARCHIVE_DATA=$PG_DATA_BASE/archive
export BACKUP_DATA=$PG_DATA_BASE/backup
export TRIGGER_FILE=$PG_DATA_BASE/bring_me_up