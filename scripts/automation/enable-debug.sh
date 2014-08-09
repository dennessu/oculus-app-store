#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
pushd $DIR

set -e

: ${ENV?"Need to set ENV"}

./foreach-here.sh $ENV/_all.txt << EOF
if ! grep '^common.conf.debugMode=' /etc/silkcloud/configuration.properties; then
    echo common.conf.debugMode=true >> /etc/silkcloud/configuration.properties
fi
sed -e 's/common\.conf\.debugMode=false/common.conf.debugMode=true/g' -i /etc/silkcloud/configuration.properties
echo Updated to \`grep '^common.conf.debugMode=' /etc/silkcloud/configuration.properties\`
EOF

echo Recycle all servers
./recycle-all.sh $ENV
