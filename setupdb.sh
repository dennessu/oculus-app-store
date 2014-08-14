#!/bin/bash
source "$(git rev-parse --show-toplevel)/scripts/common.sh"; # this comment is needed, see common.sh for detail

t0=`date +%s`
cipherKey=D58BA755FF96B35A6DABA7298F7A8CE2

dbPrefixFile=common/configuration-data/src/main/resources/junbo/conf/onebox/common/personal.properties
dbPrefix=`cat $dbPrefixFile | grep '^common.cloudant.dbNamePrefix=' | awk -F= '{gsub(/^[ \t]+/, "", $2); print $2}'`
if [[ -z "$dbPrefix" ]]; then
    echo common.cloudant.dbNamePrefix=`./scripts/AESCipher.py genkey | cut -c1-7 | tr '[:upper:]' '[:lower:]'`_ >> $dbPrefixFile
    dbPrefix=`cat $dbPrefixFile | grep '^common.cloudant.dbNamePrefix=' | awk -F= '{gsub(/^[ \t]+/, "", $2); print $2}'`
    if [[ -z "$dbPrefix" ]]; then
        echo Error generating dbPrefix
        exit 1
    fi
fi
echo "dbPrefix is $dbPrefix"

if [[ "$1" == "" || "$1" == "memcache" ]]; then
    # setup cloudant db
    pushd memcache
    python ./flush-all.py
    popd
fi


if [[ "$1" == "" || "$1" == "couch" ]]; then
    # setup cloudant db
    pushd couchdb
    python ./couchdbcmd.py dropdbs --prefix=$dbPrefix --key=$cipherKey --yes
    python ./couchdbcmd.py createdbs --prefix=$dbPrefix --key=$cipherKey --yes
    popd
fi

if [[ "$1" == "" || "$1" == "sql" ]]; then
    # setup sql db
    pushd liquibase
    ./dropdb.sh -env:onebox -key:$cipherKey
    ./createdb.sh -env:onebox -key:$cipherKey
    ./updatedb.sh -env:onebox -key:$cipherKey
    popd
fi

t1=`date +%s`
echo Setup DB Total Elapsed: $[$t1-$t0] seconds

