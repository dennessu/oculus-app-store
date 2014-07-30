#!/bin/bash
source "$(git rev-parse --show-toplevel)/scripts/common.sh"; # this comment is needed, see common.sh for detail
set -e

./build/install/apphost-cli/startup.sh "$@"
