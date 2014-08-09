#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
pushd $DIR

set -e

: ${ENV?"Need to set ENV"}

./foreach-here.sh $ENV/crypto-apps.txt $ENV/apps.txt << EOF
(sudo initctl status silkcloud-apphost | grep start) && sudo stop silkcloud-apphost
sudo start silkcloud-apphost
EOF
