#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
pushd $DIR

set -e

./foreach-here.sh ppe.txt << EOF
if ! grep '^common.conf.debugMode=' /etc/silkcloud/configuration.properties; then
    echo common.conf.debugMode=false >> /etc/silkcloud/configuration.properties
fi
sed -e 's/common\.conf\.debugMode=true/common.conf.debugMode=false/g' -i /etc/silkcloud/configuration.properties
echo Updated to \`grep '^common.conf.debugMode=' /etc/silkcloud/configuration.properties\`
EOF

echo Recycle all servers
./recycle-all.sh

