#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
pushd $DIR

set -e

export ENV=${ENV:-$1}
export ENV=${ENV:-ppe}

./foreach-here.sh $ENV/crypto-apps.txt $ENV/apps.txt << EOF
(sudo initctl status node-App | grep start) && sudo stop silkcloud-apphost
EOF
