#!/bin/bash
set -e

function error_exit {
    echo
    echo "$@"
    exit 1
}
shopt -s expand_aliases
alias die='error_exit "Error ${0}(@`echo $(( $LINENO - 1 ))`):"'

# check if the database ports can be reached
# this cannot be put into checkdockerenv.sh because that script would be executed before rinetd.sh
nc -z 127.0.0.1 11211 || die "cannot reach memcached via port 11211, cannot continue"
nc -z 127.0.0.1 5432 || die "cannot reach psql via port 5432, cannot continue"
nc -z 127.0.0.1 5984 || die "cannot reach couchdb via port 5984, cannot continue"

echo "populate db....."
# TODO
