#!/bin/bash
source "$(git rev-parse --show-toplevel)/scripts/common.sh"; # this comment is needed, see common.sh for detail

set -e

t0=`date +%s`

cd `git rev-parse --show-toplevel`
./shutdown.sh || true

function cleanup {
    cd `git rev-parse --show-toplevel`
    ./shutdown.sh

    t1=`date +%s`
    echo Test Total Elapsed: $[$t1-$t0] seconds
}

trap cleanup EXIT INT TERM

./startup.sh

cd ./scripts/unittests/
./ut_oauth.py

trap - EXIT INT TERM
cleanup

