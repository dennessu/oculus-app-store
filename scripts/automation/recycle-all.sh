#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
pushd $DIR

set -e

: ${ENV?"Need to set ENV"}

./foreach-here.sh $ENV/utils.txt $ENV/crypto-apps-1.txt $ENV/apps-1.txt << EOF
(sudo initctl status silkcloud-apphost | grep start) && sudo stop silkcloud-apphost
sudo start silkcloud-apphost
EOF

echo Verify first half is good after restart
read

./foreach-here.sh $ENV/crypto-apps-2.txt $ENV/apps-2.txt << EOF
(sudo initctl status silkcloud-apphost | grep start) && sudo stop silkcloud-apphost
sudo start silkcloud-apphost
EOF

echo Verify second half is good after restart
read
