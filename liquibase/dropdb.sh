#!/bin/bash
(set -o igncr) 2>/dev/null && set -o igncr; # ignore \r in windows. The comment is needed.

if [[ "$#" -eq 0 ]]; then
    echo "usage: $0 -env:ENVIRONMENT"
    echo "example: $0 -env:int1box"
    exit 1
fi

dbVersion=0

for dbname in `ls -d changelogs/*/$dbVersion | cut -f2 -d'/'`
do
    if [[ ! -f "changelogs/$dbname/disabled.txt" ]]; then
        python ./dbcmd.py -db:$dbname -ver:$dbVersion -cmd:drop -yes "$@"
    fi
done
