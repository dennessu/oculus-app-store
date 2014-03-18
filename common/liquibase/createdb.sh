#!/bin/sh
function createdb {
    dbname=$1

    if psql -lqt | cut -d \| -f 1 | (grep -w $dbname > /dev/null); then
        # database exists
        echo database $dbname already exists.
    else
        # create database
        echo creating database $dbname
        psql postgres postgres -c "CREATE DATABASE \"$dbname\""
    fi
}

for dbname in `ls -d changelogs/*/ | cut -f2 -d'/'`
do
    createdb ${dbname%%/}
done
