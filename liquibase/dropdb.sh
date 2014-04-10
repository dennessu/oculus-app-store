#!/bin/sh
(set -o igncr) 2>/dev/null && set -o igncr; # some comment

function dropdb {
    dbname=$1

    if psql -lqt postgres postgres | cut -d \| -f 1 | (grep -w $dbname > /dev/null); then
        # database exists
        echo database $dbname exists.
        psql -q -X -c "DROP DATABASE \"$dbname\"" postgres postgres
    else
        # create database
        echo database $dbname not exists
    fi
}

for dbname in `ls -d changelogs/*/ | cut -f2 -d'/'`
do
    dropdb ${dbname%%/}
done
