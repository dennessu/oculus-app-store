#!/bin/sh
set -e 

function createrole {
    rolename=$1
    password='#Bugsfor$'
    
    echo creating role $rolename
    psql postgres postgres -q -X -c "DO
\$body$
BEGIN
   IF NOT EXISTS (SELECT * FROM pg_catalog.pg_roles WHERE rolname = '$rolename') THEN
      CREATE ROLE $rolename LOGIN PASSWORD '$password';
   END IF;
END
\$body$"

}

function createschema {
    dbname=$1
    schemaname=$2
    rolename=$2

    echo creating schema $dbname.$schemaname
    createrole $rolename
    psql $dbname postgres -q -X -c "CREATE SCHEMA IF NOT EXISTS $schemaname"
    psql $dbname postgres -q -X -c "ALTER SCHEMA $schemaname OWNER TO $rolename"
    psql $dbname postgres -q -X -c "ALTER ROLE $rolename SET search_path=$schemaname;"
}

function createdb {
    dbname=$1

    if psql -lqt | cut -d \| -f 1 | (grep -w $dbname > /dev/null); then
        # database exists
        echo database $dbname already exists.
    else
        # create database
        echo creating database $dbname
        psql postgres postgres -q -X -c "CREATE DATABASE \"$dbname\""
        
        if [ "$dbname" == "config" ] || [ "$dbname" == "catalog" ]; then
            createschema $dbname $dbname
        elif [ "$dbname" == "index" ]; then
            createschema $dbname schema_0
            createschema $dbname schema_1
        else
            createschema $dbname shard_0
            createschema $dbname shard_1
        fi
    fi
}

for dbname in `ls -d changelogs/*/ | cut -f2 -d'/'`
do
    createdb ${dbname%%/}
done
