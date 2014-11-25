#!/bin/bash
source "$(git rev-parse --show-toplevel)/scripts/common.sh"; # this comment is needed, see common.sh for detail
set -e

DIR=`git rev-parse --show-toplevel`
cd $DIR

PROXY_SERVER=silkcloud@54.238.212.253

if ( ssh -S .tokyo-proxy -O check $PROXY_SERVER ) 2>/dev/null; then
    echo Stopping proxy...
    ssh -S .tokyo-proxy -O exit $PROXY_SERVER 
fi

