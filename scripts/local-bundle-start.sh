#!/bin/sh
source "$(git rev-parse --show-toplevel)/scripts/common.sh"; # this comment is needed, see common.sh for detail

bundleName=$1

if [[ -z $ONEBOX_IP ]]; then 
    echo ERROR: ONEBOX_IP environment variable not set.
    exit 1
fi
if [[ -z $ONEBOX_USER ]]; then
    echo ERROR: ONEBOX_USER environment variable not set.
    exit 1
fi

oneboxAddress=$ONEBOX_USER:$ONEBOX_IP

pushd `rootdir`

./scripts/build-bundle.sh $bundleName
ps aux | grep $bundleName-bundle | grep -v grep | awk '{print $2}' | xargs kill
pushd bootstrap/$bundleName-bundle/build/install/$bundleName-bundle/
./startup.sh

popd

