#!/bin/bash
source "$(git rev-parse --show-toplevel)/scripts/common.sh"; # this comment is needed, see common.sh for detail

cd `git rev-parse --show-toplevel`
cd apphost/apphost-cli/build/install/apphost-cli/
./shutdown.sh || true
./startup.sh onebox

tail -f -n0 logs/main.log | while read LOGLINE
do
    if [[ "${LOGLINE}" == *"Started Junbo Application Host"* ]]; then
        ps -aef | grep tail | grep $$ | grep -v grep | awk '{print $2}' | xargs kill
    fi
done
./dataloader.sh

function cleanup {
    cd `git rev-parse --show-toplevel`
    cd apphost/apphost-cli/build/install/apphost-cli/ 
    ./shutdown.sh
}

trap cleanup EXIT INT TERM

cd `git rev-parse --show-toplevel`/integrationtesting
gradle

trap - EXIT INT TERM
cleanup

