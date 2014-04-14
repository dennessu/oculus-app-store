#!/bin/sh
source "$(git rev-parse --show-toplevel)/scripts/common.sh"; # this comment is needed, see common.sh for detail

function dropdb {
    dbname=$1

    # for each database
    if psqlh_d -lt | cut -d \| -f 1 | (grep -w $dbname > /dev/null); then
        # database exists
        echo database $dbname exists.
        psqlh_d -c "DROP DATABASE \"$dbname\""
    else
        # create database
        echo database $dbname not exists
    fi
}

dropdb $*
