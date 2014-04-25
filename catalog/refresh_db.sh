#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
pushd $DIR/../liquibase
python ./dbcmd.py catalog onebox 0 drop --yes
python ./dbcmd.py catalog onebox 0 create --yes
python ./dbcmd.py catalog onebox 0 update --yes
popd
