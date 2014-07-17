#!/bin/bash
source "$(git rev-parse --show-toplevel)/scripts/common.sh"; # this comment is needed, see common.sh for detail

bundleName=$1

pushd `rootdir`/bootstrap/$bundleName-bundle
gradle installApp distTar
popd

