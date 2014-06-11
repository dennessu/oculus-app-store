#!/bin/bash

# set environment variables
export DEVOPS_ACCOUNT='ubuntu'
export DEPLOYMENT_ACCOUNT='silkcloud'
export DEPLOYMENT_PATH='/tmp/pgha'

export DATA_PATH='/tmp/data'
export BACKUP_PATH='/tmp/backup'
export ARCHIVE_PATH='/tmp/archive'
export CRON_PATH='/tmp/pgcron'

export SKYTOOL_PATH='/tmp/skytool'
export SKYTOOL_CONFIG_PATH=$SKYTOOL_PATH/config
export SKYTOOL_PID_PATH=$SKYTOOL_PATH/pid
export SKYTOOL_LOG_PATH=$SKYTOOL_PATH/log

export PGBIN_PATH='/usr/lib/postgresql/9.3/bin'
export PGLOCK_PATH='/run/postgresql'
export PROMOTE_TRIGGER_FILE='/tmp/bring_me_up'
export PGUSER='silkcloud'

export DB_PORT=5432

#master info
export MASTER_HOST=54.254.246.13
export MASTER_DB_PORT=$DB_PORT
export MASTER_DATA_PATH=$DATA_PATH
export MASTER_BACKUP_PATH=$BACKUP_PATH
export MASTER_ARCHIVE_PATH=$ARCHIVE_PATH

#slave info
export SLAVE_HOST=54.255.148.38
export SLAVE_DB_PORT=$DB_PORT
export SLAVE_DATA_PATH=$DATA_PATH
export SLAVE_BACKUP_PATH=$BACKUP_PATH
export SLAVE_ARCHIVE_PATH=$ARCHIVE_PATH

#replica info
export REPLICA_HOST=54.255.213.75
export REPLICA_DB_PORT=$DB_PORT
export REPLICA_DATA_PATH=$DATA_PATH
export REPLICA_ARCHIVE_PATH=$ARCHIVE_PATH

#pgbouncer
export PRIMARY_PGBOUNCER_HOST=$MASTER_HOST
export SECONDARY_PGBOUNCER_HOST=$SLAVE_HOST
export REPLICA_DATABASES=('repl_test')

export PGBOUNCER_PORT=6543

export PGBOUNCER_BIN=/usr/sbin
export PGBOUNCER_BASE='/tmp/pgbouncer'
export PGBOUNCER_CONF=$PGBOUNCER_BASE/pgbouncer.conf
export PGBOUNCER_PID=$PGBOUNCER_BASE/pgbouncer.pid
export PGBOUNCER_AUTH_FILE=~/pgbouncer_auth.txt
export PGBOUNCER_SOCKET_PATH='/tmp'

export PGBOUNCER_MAX_CONNECTIONS=100
export PGBOUNCER_DEFAULT_POOL_SIZE=100

# stop the execution whenever there is an error
set -e

# kill process with specified port
function forceKill {
    if (fuser -n tcp $1 2> /dev/null)
    then
        kill -9 $(fuser -n tcp $1 2> /dev/null)
    else
        echo "no process running with [$1] port..."
    fi    
}

function forceKillPid {
    set +e

    if [ -f $1 ];then
        cat $1 | xargs echo
        cat $1 | xargs kill -9
    elif [ -d $1 ];then
        for f in `ls $1`
        do
            cat $1/$f | xargs echo
            cat $1/$f | xargs kill -9
        done
    else
        echo "path [$1] does not exist"
    fi

    set -e
}

# check running account
function checkAccount {
    if [ "$(whoami)" != "$1" ]; then
        echo "this script must be run as $1"
        exit 1
    fi
}

# create directory
function createDir {
    rm -rf $1
    mkdir $1
    chmod 700 $1
}
