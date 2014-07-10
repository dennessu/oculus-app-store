#!/bin/bash
(set -o igncr) 2>/dev/null && set -o igncr; # ignore \r in windows. The comment is needed.

dbVersion=0

for dbname in `ls -d changelogs/*/$dbVersion | cut -f2 -d'/'`
do
    if [[ ! -f "changelogs/$dbname/disabled.txt" ]]; then
        python ./dbcmd.py -db:$dbname -ver:$dbVersion -cmd:create -yes "$@"
    fi
done
