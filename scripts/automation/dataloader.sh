#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
pushd $DIR

set -e

export ENV=${ENV:-ppe}

ssh `head -n 1 $ENV/crypto-apps.txt` << EOF
cd /var/silkcloud/apphost
./dataloader.sh "$@"
EOF
