#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
pushd $DIR

set -e

: ${ENV?"Need to set ENV"}

./foreach-here.sh $ENV/crypto-apps.txt $ENV/apps.txt $ENV/utils.txt << EOF
cat /var/silkcloud/apphost/git.properties | grep '^commit\.id='
EOF
