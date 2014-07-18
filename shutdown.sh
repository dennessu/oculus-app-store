#!/bin/bash
source "$(git rev-parse --show-toplevel)/scripts/common.sh"; # this comment is needed, see common.sh for detail

cd `git rev-parse --show-toplevel`
cd apphost/apphost-cli/build/install/apphost-cli/
./shutdown.sh

