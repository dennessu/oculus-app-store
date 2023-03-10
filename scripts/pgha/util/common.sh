#!/bin/bash
DIR="$( cd "$( dirname "$BASH_SOURCE[0]" )" && pwd )"

# stop the execution whenever there is an error
set -e

if [[ -z "$ENVIRONMENT" ]]; then
    CONFIG_FILE=/etc/silkcloud/configuration.properties
    if [[ -f $CONFIG_FILE ]]; then
        ENVIRONMENT=`cat $CONFIG_FILE | grep '^environment=' | sed 's/environment=//'`
    fi
    if [[ -z "$ENVIRONMENT" ]]; then
        echo "Environment is not found in $CONFIG_FILE"
        exit 1
    fi
fi

# parameters overridable by env file
# pgsql conf
export MAX_CONNECTIONS=1200
export SHARED_BUFFERS='128MB'
export MAINTENANCE_WORK_MEM='16MB'
export EFFECTIVE_CACHE_SIZE='128MB'
export CHECKPOINT_SEGMENTS='3'
export CHECKPOINT_COMPLETION_TARGET='0.5'

ENVIRONMENT_FILE=$DIR/../env/${ENVIRONMENT}.sh
if [[ -f $ENVIRONMENT_FILE ]]; then
    source $ENVIRONMENT_FILE
fi

export DEVOPS_ACCOUNT='devops'
export DEPLOYMENT_ACCOUNT='silkcloud'

export SILKCLOUD_BASE='/var/silkcloud'
export SILKCLOUD_OPS='/var/ops'

export DEPLOYMENT_PATH=$SILKCLOUD_BASE/pgha
export PGHA_BASE=$SILKCLOUD_BASE/postgresql

export DATA_PATH=$PGHA_BASE/data
export BACKUP_PATH=$PGHA_BASE/backup
export ARCHIVE_PATH=$PGHA_BASE/archive
export CRON_PATH=$PGHA_BASE/pgron
export DB_LOG_PATH=$SILKCLOUD_BASE/logs/postgresql

export SKYTOOL_PATH=$PGHA_BASE/skytool
export SKYTOOL_CONFIG_PATH=$SKYTOOL_PATH/config
export SKYTOOL_PID_PATH=$SKYTOOL_PATH/pid
export SKYTOOL_LOG_PATH=$SILKCLOUD_BASE/logs/skytool

export PGBIN_PATH='/usr/lib/postgresql/9.3/bin'
export PGLOCK_PATH='/run/postgresql'
export PROMOTE_TRIGGER_FILE=$PGHA_BASE/bring_me_up
export PGUSER='silkcloud'
export READONLY_PGUSER='scro'
export NEWRELIC_PGUSER='newrelic'
export ZABBIX_PGUSER='zabbix'
export DB_PORT=5432

#master info
export MASTER_DB_PORT=$DB_PORT
export MASTER_DATA_PATH=$DATA_PATH
export MASTER_BACKUP_PATH=$BACKUP_PATH
export MASTER_LOG_PATH=$DB_LOG_PATH

#slave info
export SLAVE_DB_PORT=$DB_PORT
export SLAVE_DATA_PATH=$DATA_PATH
export SLAVE_BACKUP_PATH=$BACKUP_PATH
export SLAVE_LOG_PATH=$DB_LOG_PATH

#bcp info
export BCP_DB_PORT=$DB_PORT
export BCP_DATA_PATH=$DATA_PATH
export BCP_BACKUP_PATH=$BACKUP_PATH
export BCP_LOG_PATH=$DB_LOG_PATH

#replica info
export REPLICA_DB_PORT=$DB_PORT
export REPLICA_DATA_PATH=$DATA_PATH
export REPLICA_LOG_PATH=$DB_LOG_PATH

#pgbouncer
export PRIMARY_PGBOUNCER_HOST=$MASTER_HOST
export SECONDARY_PGBOUNCER_HOST=$SLAVE_HOST

export PGBOUNCER_PORT=6543

export PGBOUNCER_BIN=/usr/sbin
export PGBOUNCER_BASE=$PGHA_BASE/pgbouncer
export PGBOUNCER_PID=$PGBOUNCER_BASE/pgbouncer.pid
export PGBOUNCER_AUTH_FILE=~/.pgbouncer_auth
export PGBOUNCER_SOCKET_PATH='/tmp'

#archive restore command
export ARCHIVE_COMMAND="'cp %p $ARCHIVE_PATH/%f'"
export RESTORE_COMMAND="'cp $ARCHIVE_PATH/%f %p'"

#http://pgbouncer.projects.pgfoundry.org/doc/config.html
export PGBOUNCER_MAX_CONNECTIONS=$MAX_CONNECTIONS
export PGBOUNCER_DEFAULT_POOL_SIZE=$MAX_CONNECTIONS

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
        cat $1 | xargs kill -9 || echo 'process does not exist'
    elif [ -d $1 ];then
        for f in `ls $1`
        do
            cat $1/$f | xargs echo
            cat $1/$f | xargs kill -9 || echo 'process does not exist'
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
    mkdir -p $1
    chmod 700 $1
}

# get server role
function getServerRole {
    echo `cat $PGHA_BASE/role.conf`
}

# check server role
function checkServerRole {
    role=`getServerRole`
    echo "The server is [$role]"

    if [[ $role != "$1" ]] ; then
        echo "the script should be executed on [$1] server"
        exit 1
    fi
}

# start database
function startDB {
    echo "database data file path [$1]"
    echo "database log file path [$2]"

    rm -f $1/postmaster.pid
    $PGBIN_PATH/pg_ctl -D $1 -l "$2/postgresql-$(date +%Y-%m-%d:%H:%M:%S.%N).log" start > /dev/null 2>&1 &
}

# shutdown database
function stopDB {
    echo "database data file path [$1]"
    $PGBIN_PATH/pg_ctl stop -m fast -D $1
}
