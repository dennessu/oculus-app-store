#!/bin/bash
source "$(git rev-parse --show-toplevel)/scripts/common.sh"; # this comment is needed, see common.sh for detail

t0=`date +%s`
. ./setupproxy.sh
./fullcycle.sh "-x build $@"

# source setupproxy to get the exported proxy variables
./test.sh

t1=`date +%s`
echo Supercycle Total Elapsed: $[$t1-$t0] seconds

