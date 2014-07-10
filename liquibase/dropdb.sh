#!/bin/bash
(set -o igncr) 2>/dev/null && set -o igncr; # ignore \r in windows. The comment is needed.

dbVersion=0

environment=$1
if [[ "$environment" == "" ]]; then
    environment=onebox
fi

for dbname in `ls -d changelogs/*/$dbVersion | cut -f2 -d'/'`
do
    if [[ ! -f "changelogs/$dbname/disabled.txt" ]]; then
        python ./dbcmd.py $dbname $environment $dbVersion drop --yes
    fi
done
