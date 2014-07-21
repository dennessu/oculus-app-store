#!/bin/bash
source "$(git rev-parse --show-toplevel)/scripts/common.sh"; # this comment is needed, see common.sh for detail

cd `git rev-parse --show-toplevel`
cd apphost/apphost-cli/build/install/apphost-cli/
./shutdown.sh || true

function cleanup {
    cd `git rev-parse --show-toplevel`
    cd apphost/apphost-cli/build/install/apphost-cli/
    ./shutdown.sh
}

trap cleanup EXIT INT TERM

./startup.sh onebox

cd `git rev-parse --show-toplevel`/integrationtesting
gradle

trap - EXIT INT TERM
cleanup
