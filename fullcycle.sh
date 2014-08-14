#!/bin/bash
source "$(git rev-parse --show-toplevel)/scripts/common.sh"; # this comment is needed, see common.sh for detail
set -e

t0=`date +%s`
./setupdb.sh
./build.sh "$@"
./dataloader.sh

t1=`date +%s`
echo Total Elapsed: $[$t1-$t0] seconds

