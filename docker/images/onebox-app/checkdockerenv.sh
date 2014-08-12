#!/bin/bash
set -e

# check docker link settings and silkcloud config
# put this script into /etc/init.d/, so that it would be executed during boot

function error_exit {
    echo
    echo "$@"
    exit 1
}
shopt -s expand_aliases
alias die='error_exit "Error ${0}(@`echo $(( $LINENO - 1 ))`):"'

# check if psql is linked correctly
: ${PSQL_PORT:? "Env var PSQL_PORT not found, have you linked psql container?"}
if ! grep -q psql /etc/hosts; then
  die "psql not found in hosts"
fi

# check if memcached is linked correctly
: ${MEMCACHED_PORT:? "Env var MEMCACHED_PORT not found, have you linked memcached container?"}
if ! grep -q memcached /etc/hosts; then
  die "memcached not found in hosts"
fi

# check if couchdb is linked correctly
: ${COUCHDB_PORT:? "Env var COUCHDB_PORT not found, have you linked couchdb container?"}
if ! grep -q couchdb /etc/hosts; then
  die "couchdb not found in hosts"
fi

# TODO: check configurations
#  extract prefix and environment from config files

echo "check done, good to go:)"
