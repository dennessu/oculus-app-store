#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
pushd $DIR

set -e

export ENV=${ENV:-ppe}

ARGS=${1:-all}

ssh `head -n 1 $ENV/crypto-apps.txt` << EOF
cd /var/silkcloud/apphost
./dataloader.sh "$ARGS"
EOF
