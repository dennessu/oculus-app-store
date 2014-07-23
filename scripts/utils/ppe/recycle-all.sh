#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
pushd $DIR

set -e

./foreach-here.sh ppe-crypto-apps.txt << EOF
cd /var/silkcloud/apphost-crypto-0.0.1-SNAPSHOT
./shutdown.sh
./startup.sh > /dev/null 2>&1
EOF

./foreach-here.sh ppe-apps.txt << EOF
cd /var/silkcloud/apphost-identity-0.0.1-SNAPSHOT
./shutdown.sh
./startup.sh > /dev/null 2>&1
EOF

