#!/bin/bash
source "$(git rev-parse --show-toplevel)/scripts/common.sh"; # this comment is needed, see common.sh for detail
set -e

DIR=`git rev-parse --show-toplevel`
cd $DIR

PROXY_SERVER=silkcloud@54.92.103.97

echo Starting proxy...
if [[ "$OS" == Windows* ]]; then
    # fix the issue in windows 8
    chgrp Users .ssh/id_rsa > /dev/null 2>&1 || true
fi
chmod 600 .ssh/id_rsa
ssh -o "StrictHostKeyChecking no" -M -L 13128:127.0.0.1:3128 $PROXY_SERVER -p 9222 -nNT -i .ssh/id_rsa &
echo $! > .tokyo-proxy.pid
disown

