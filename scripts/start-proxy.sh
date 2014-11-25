#!/bin/bash
source "$(git rev-parse --show-toplevel)/scripts/common.sh"; # this comment is needed, see common.sh for detail
set -e

DIR=`git rev-parse --show-toplevel`
cd $DIR

PROXY_SERVER=silkcloud@54.92.103.97

if ! ( ssh -S .tokyo-proxy -O check $PROXY_SERVER ) 2>/dev/null; then
    echo Starting proxy...
    chmod 600 .ssh/id_rsa
    ssh -M -S .tokyo-proxy -L 13128:127.0.0.1:3128 $PROXY_SERVER -p 9222 -fnNT -i .ssh/id_rsa
else
    echo Proxy already started.
fi

