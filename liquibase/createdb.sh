#!/bin/bash
source "$(git rev-parse --show-toplevel)/scripts/common.sh"; # this comment is needed, see common.sh for detail

dbVersion=0

environment=$1
if [[ "$environment" == "" ]]; then
    environment=onebox
fi

for dbname in `ls -d changelogs/*/$dbVersion | cut -f2 -d'/'`
do
    python ./dbcmd.py $dbname $environment $dbVersion create --yes
done
