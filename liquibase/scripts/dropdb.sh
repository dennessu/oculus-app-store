#!/bin/bash
source "$( dirname "${BASH_SOURCE[0]}" )/pgcommon.sh" # ignore \r in windows. The comment is needed.

function dropdb {
    dbname=$1
    host=$2
    port=$3

    if [[ "$host" == "" ]]; then
        host=localhost
    fi

    if [[ "$port" == "" ]]; then
        port=5432
    fi

    # for each database
    if psqlh_d -lt -h $host -p $port | cut -d \| -f 1 | (grep -w $dbname > /dev/null); then
        # database exists
        echo database $dbname exists.
        psqlh_d -c "DROP DATABASE \"$dbname\"" -h $host -p $port
    else
        # create database
        echo database $dbname not exists
    fi
}

dropdb $*
