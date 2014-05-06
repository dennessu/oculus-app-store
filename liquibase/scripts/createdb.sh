#!/bin/sh
source "$(git rev-parse --show-toplevel)/scripts/common.sh"; # this comment is needed, see common.sh for detail

function createschema {
    dbname=$1
    schemaname=$2

    echo creating schema $dbname.$schemaname
    psqlh $dbname -c "CREATE SCHEMA IF NOT EXISTS $schemaname"
}

function createdb {
    dbname=$1
    schema=$2

    if psqlh_d -lt | cut -d \| -f 1 | (grep -w $dbname > /dev/null); then
        # database exists
        echo database $dbname already exists.
    else
        # create database
        echo creating database $dbname
        psqlh_d -c "CREATE DATABASE \"$dbname\""
    fi
        
    createschema "$dbname" "$schema"
}

createdb $*
