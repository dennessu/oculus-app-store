#!/bin/bash
if [[ -z $1 ]]; then
    echo Usage: dropall-cloudant.sh \<prefix\>
    exit 1
fi
python ./couchdbcmd.py dropall 'https://silkcloudtest1:#Bugsfor$@silkcloudtest1.cloudant.com' $1
