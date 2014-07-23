#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
pushd $DIR

set -e

ssh 10.24.32.10 << EOF
cd /var/silkcloud
cd apphost-dataloader-0.0.1-SNAPSHOT
./dataloader.sh "$@"
EOF

