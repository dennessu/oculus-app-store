#!/bin/bash
source "$(git rev-parse --show-toplevel)/scripts/common.sh"; # this comment is needed, see common.sh for detail

function createschema {
    dbname=$1
    schemaname=$2
    host=$3

    echo creating schema $dbname.$schemaname
    psqlh $dbname -c "CREATE SCHEMA IF NOT EXISTS $schemaname" -h $host
}

function createdb {
    dbname=$1
    schema=$2
    host=$3

    if [[ "$host" == "" ]]; then
        host=localhost
    fi

    if psqlh_d -lt -h $host | cut -d \| -f 1 | (grep -w $dbname > /dev/null); then
        # database exists
        echo database $dbname already exists.
    else
        # create database
        echo creating database $dbname
        psqlh_d -c "CREATE DATABASE \"$dbname\"" -h $host
    fi
        
    createschema "$dbname" "$schema" "$host"
}

createdb $*
