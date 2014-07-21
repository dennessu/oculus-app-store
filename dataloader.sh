#!/bin/bash
source "$(git rev-parse --show-toplevel)/scripts/common.sh"; # this comment is needed, see common.sh for detail

cd `git rev-parse --show-toplevel`
pushd apphost/apphost-cli/build/install/apphost-cli/
./dataloader.sh "$@"
popd
