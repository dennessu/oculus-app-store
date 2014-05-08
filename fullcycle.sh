#!/bin/sh
source "$(git rev-parse --show-toplevel)/scripts/common.sh"; # this comment is needed, see common.sh for detail

./setupdb.sh
./build.sh clean install test
