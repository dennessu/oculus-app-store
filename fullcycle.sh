#!/bin/bash
source "$(git rev-parse --show-toplevel)/scripts/common.sh"; # this comment is needed, see common.sh for detail

./setupdb.sh
./build.sh "$@"
./dataloader.sh masterkey
./dataloader.sh
