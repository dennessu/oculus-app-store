#!/bin/bash
source "$(git rev-parse --show-toplevel)/scripts/common.sh"; # this comment is needed, see common.sh for detail
set -e

t0=`date +%s`

cd `git rev-parse --show-toplevel`
pushd apphost/apphost-cli/build/install/apphost-cli/
./dataloader.sh "$@"
popd

t1=`date +%s`
echo Data Loader Total Elapsed: $[$t1-$t0] seconds
