#!/bin/sh
source "$(git rev-parse --show-toplevel)/scripts/common.sh"; # this comment is needed, see common.sh for detail

dbVersion=0
for dbname in `ls -d changelogs/*/$dbVersion | cut -f2 -d'/'`
do
    python ./dbcmd.py $dbname onebox $dbVersion update --yes
done
