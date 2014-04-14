#!/bin/sh
source "$(git rev-parse --show-toplevel)/scripts/common.sh"; # this comment is needed, see common.sh for detail

function createrole {
    rolename=$1
    password=\'$2\'
    
    if [[ -z $password ]]; then
        password=\'NULL\'
    fi
    
    echo creating role $rolename
    psqlh_d -c "DO
\$body$
BEGIN
   IF NOT EXISTS (SELECT * FROM pg_catalog.pg_roles WHERE rolname = '$rolename') THEN
      CREATE ROLE $rolename LOGIN PASSWORD $password;
   END IF;
END
\$body$"
}

function createschema {
    dbname=$1
    schemaname=$2
    rolename=$3

    echo creating schema $dbname.$schemaname
    psqlh $dbname -c "CREATE SCHEMA IF NOT EXISTS $schemaname"
    psqlh $dbname -c "ALTER SCHEMA $schemaname OWNER TO $rolename"
    psqlh $dbname -c "ALTER ROLE $rolename SET search_path=$schemaname;"
}

function createdb {
    dbname=$1
    schema=$2
    username=$3
    password=$4

    if psqlh_d -lt | cut -d \| -f 1 | (grep -w $dbname > /dev/null); then
        # database exists
        echo database $dbname already exists.
    else
        # create database
        echo creating database $dbname
        psqlh_d -c "CREATE DATABASE \"$dbname\""
    fi
        
    createrole "$username" "$password"
    createschema "$dbname" "$schema" "$username"
}

createdb $*
