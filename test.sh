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

function cleanupTrap {
    cleanup
    echo !!!!!!!!!!!!!!!!
    echo Test has ERRORs!
    echo !!!!!!!!!!!!!!!!
}

trap cleanup EXIT INT TERM

./startup.sh

cd ./scripts/unittests/
mkdir -p logs
python ./ut_oauth.py | tee logs/ut_oauth.log
python ./ut_checkout.py | tee logs/ut_checkout.log

trap - EXIT INT TERM
cleanup

echo -------------------
echo Test is Successful!
echo -------------------

