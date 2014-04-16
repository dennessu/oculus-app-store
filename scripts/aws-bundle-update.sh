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

oneboxAddress=$ONEBOX_USER@$ONEBOX_IP

pushd `rootdir`/bootstrap/$bundleName-bundle

bundlePath=build/distributions/$bundleName-bundle-0.0.1-SNAPSHOT.tar.gz
if [[ ! -f $bundlePath ]]; then
    gzip build/distributions/$bundleName-bundle-0.0.1-SNAPSHOT.tar
fi
scp $bundlePath $oneboxAddress:/opt
ssh $oneboxAddress << ENDSSH
ps aux | grep '$bundleName-bundle' | grep -v grep | awk '{print \$2}' | xargs kill
cd /opt
tar zxfv $bundleName-bundle-0.0.1-SNAPSHOT.tar.gz
cd $bundleName-bundle-0.0.1-SNAPSHOT
./startup.sh
ENDSSH

popd

