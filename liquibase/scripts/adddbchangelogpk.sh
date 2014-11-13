#!/bin/bash
source "$( dirname "${BASH_SOURCE[0]}" )/pgcommon.sh" # ignore \r in windows. The comment is needed.

function createdbchangelogpk {
    dbname=$1
    schemaname=$2
    host=$3
    port=$4

    echo creating primary key to $dbname.$schemaname.databasechangelog
    psqlh $dbname -h $host -p $port <<EOF
DO
\$\$
BEGIN
    IF NOT EXISTS (
        SELECT 'x' FROM pg_indexes
        WHERE schemaname = '$schemaname'
          AND tablename = 'databasechangelog'
    ) THEN
        ALTER TABLE "$schemaname".databasechangelog ADD PRIMARY KEY (id, author, filename);
    END IF;

    IF NOT EXISTS (
        SELECT 'x' FROM pg_indexes
        WHERE schemaname = '$schemaname'
          AND tablename = 'databasechangeloglock'
    ) THEN
        ALTER TABLE "$schemaname".databasechangeloglock ADD PRIMARY KEY (id);
    END IF;
END;
\$\$
EOF
}

createdbchangelogpk $*
