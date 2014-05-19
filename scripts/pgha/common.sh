#!/bin/bash

# set environment variables
export USERNAME=ubuntu
export DEPLOYMENT_PATH='/tmp/pgha'
export DATA_PATH='/tmp/data'
export BACKUP_PATH='/tmp/backup'
export ARCHIVE_PATH='/tmp/archive'

export MASTER_HOST=54.254.246.13
export MASTER_ADDRESS=$USERNAME@$MASTER_HOST
export MASTER_DEPLOYMENT_PATH=$MASTER_ADDRESS:$DEPLOYMENT_PATH
export MASTER_DB_PORT=5432
export MASTER_DATA_PATH=$DATA_PATH
export MASTER_BACKUP_PATH=$BACKUP_PATH
export MASTER_ARCHIVE_PATH=$ARCHIVE_PATH

export SLAVE_HOST=54.255.148.38
export SLAVE_ADDRESS=$USERNAME@$SLAVE_HOST
export SLAVE_DEPLOYMENT_PATH=$SLAVE_ADDRESS:$DEPLOYMENT_PATH
export SLAVE_DB_PORT=5432
export SLAVE_DATA_PATH=$DATA_PATH
export SLAVE_BACKUP_PATH=$BACKUP_PATH
export SLAVE_ARCHIVE_PATH=$ARCHIVE_PATH

export PRIMARY_PGBOUNCER_HOST=$MASTER_HOST
export PRIMARY_PGBOUNCER_PORT=6543

export SECONDARY_PGBOUNCER_HOST=$SLAVE_HOST
export SECONDARY_PGBOUNCER_PORT=6543

# stop the execution whenever there is an error
set -e

# kill process with specified port
function forceKill {
	port=$1

	if (lsof -i:$port -t)
    then
	    kill -n 9 $(lsof -i:$port -t)
    else
	    echo "no process running with [$port] port..."
    fi
}