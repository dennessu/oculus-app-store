#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
pushd $DIR

set -e

: ${ENV?"Need to set ENV"}

ARGS=${1:-all}

ssh `head -n 1 $ENV/crypto-apps.txt` << EOF
cd /var/silkcloud/apphost
./dataloader.sh "$ARGS"
EOF
