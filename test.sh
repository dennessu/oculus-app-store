#!/bin/bash
source "$(git rev-parse --show-toplevel)/scripts/common.sh"; # this comment is needed, see common.sh for detail

cd `git rev-parse --show-toplevel`
./shutdown.sh || true

function cleanup {
    cd `git rev-parse --show-toplevel`
    ./shutdown.sh
}

trap cleanup EXIT INT TERM

./startup.sh

cd ./scripts/unittests/
./ut_oauth.py

trap - EXIT INT TERM
cleanup
