#!/bin/bash
pushd ./../liquibase
python ./dbcmd.py subscription onebox 0 drop --yes
python ./dbcmd.py subscription onebox 0 create --yes
python ./dbcmd.py subscription onebox 0 update --yes
popd
