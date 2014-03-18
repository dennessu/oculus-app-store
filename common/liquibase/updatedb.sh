#!/bin/sh
set -e

function updatedb {
    dbname=$1

    python ./updatedb.py $dbname onebox 0 <<EOF
update
yes
EOF
}

for dbname in `ls -d changelogs/*/ | cut -f2 -d'/'`
do
    updatedb ${dbname%%/}
done
