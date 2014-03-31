#!/bin/sh
function dropdb {
    dbname=$1

    if psql -lqt | cut -d \| -f 1 | (grep -w $dbname > /dev/null); then
        # database exists
        echo database $dbname exists.
        psql postgres postgres -q -X -c "DROP DATABASE \"$dbname\""
    else
        # create database
        echo database $dbname not exists
    fi
}

for dbname in `ls -d changelogs/*/ | cut -f2 -d'/'`
do
    dropdb ${dbname%%/}
done
