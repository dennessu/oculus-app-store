#!/bin/bash

# set environment variables
export USERNAME=ubuntu

export DEPLOYMENT_ACCOUNT='silkcloud'
export DEPLOYMENT_PATH='/tmp/pgha'

export DATA_PATH='/tmp/data'
export BACKUP_PATH='/tmp/backup'
export ARCHIVE_PATH='/tmp/archive'
export CRON_PATH='/tmp/pgcron'

export PGBIN_PATH='/usr/lib/postgresql/9.3/bin'
export PGLOCK_PATH='/run/postgresql'
export PGUSER='silkcloud'
export PROMOTE_TRIGGER_FILE='/tmp/bring_me_up'

#master/slave db
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

#pgbouncer
export PRIMARY_PGBOUNCER_HOST=$MASTER_HOST
export SECONDARY_PGBOUNCER_HOST=$SLAVE_HOST

export PGBOUNCER_PORT=6543

export PGBOUNCER_BIN=/usr/sbin
export PGBOUNCER_BASE='/tmp/pgbouncer'
export PGBOUNCER_CONF=$PGBOUNCER_BASE/pgbouncer.conf
export PGBOUNCER_PID=$PGBOUNCER_BASE/pgbouncer.pid
export PGBOUNCER_AUTH_FILE=$PGBOUNCER_BASE/userlist.txt
export PGBOUNCER_SOCKET_PATH='/tmp'

export PGBOUNCER_MAX_CONNECTIONS=100
export PGBOUNCER_DEFAULT_POOL_SIZE=100

# stop the execution whenever there is an error
set -e

# kill process with specified port
function forceKill {
	if (fuser -n tcp $1 2> /dev/null)
    then
	    kill $(fuser -n tcp $1 2> /dev/null)
    else
	    echo "no process running with [$1] port..."
    fi    
}

# check shell running account
function checkAccount {
	if [ "$(whoami)" != "$1" ]; then
   		echo "this script must be run as $1"
   		exit 1
	fi
}