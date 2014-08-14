#!/bin/bash
(set -o igncr) 2>/dev/null && set -o igncr; # ignore \r in windows. The comment is needed.
set -e
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

if [[ "$#" -eq 0 ]]; then
    echo "usage: $0 -env:ENVIRONMENT"
    echo "example: $0 -env:onebox.int"
    exit 1
fi

dbVersion=0

cd $DIR
for dbname in `ls -d changelogs/*/$dbVersion | cut -f2 -d'/'`
do
    if [[ ! -f "changelogs/$dbname/disabled.txt" ]]; then
        python ./dbcmd.py -db:$dbname -ver:$dbVersion -cmd:update -yes "$@" &
    fi
done
cd -

FAILED=0
for job in `jobs -p`; do
    wait $job || let "FAILED+=1"
done

if [ "$FAILED" != "0" ]; then
    exit 1
fi
