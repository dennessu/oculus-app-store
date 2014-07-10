#!/bin/sh
source "$(git rev-parse --show-toplevel)/scripts/common.sh"; # this comment is needed, see common.sh for detail

if [[ "$1" == "" || "$1" == "cloudant" ]]; then
    # setup cloudant db
    dbPrefixFile=common/configuration-data/src/main/resources/junbo/conf/onebox/common/personal.properties
    dbPrefix=`cat $dbPrefixFile | grep '^common.cloudant.dbNamePrefix=' | sed 's/common.cloudant.dbNamePrefix=//'`
    pushd cloudant
    python ./couchdbcmd.py dropdbs --prefix=$dbPrefix --yes
    python ./couchdbcmd.py createdbs --prefix=$dbPrefix --yes
    popd
fi

if [[ "$1" == "" || "$1" == "sql" ]]; then
    # setup sql db
    pushd liquibase
    cipherKey=D58BA755FF96B35A6DABA7298F7A8CE2
    ./dropdb.sh -env:onebox -key:$cipherKey
    ./createdb.sh -env:onebox -key:$cipherKey
    ./updatedb.sh -env:onebox -key:$cipherKey
    popd
fi
