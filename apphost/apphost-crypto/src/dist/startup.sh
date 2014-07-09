#!/usr/bin/env bash
(set -o igncr) 2>/dev/null && set -o igncr; # ignore \r in windows. The comment is needed.
DIR="$( cd "$( dirname "$0" )" && pwd )"

if [ ! -d "./logs" ]; then
    mkdir logs
fi

export APPHOST_CRYPTO_OPTS="-DconfigDir=/etc/silkcloud/:./conf"

# check environment
silkcloudenv=$1
if [[ "$silkcloudenv" == "" ]]; then
    if ! grep '^environment=[a-zA-Z0-9_]\+' $DIR/conf/configuration.properties; then
        echo "ERROR: environment not set!"
        exit 1
    fi
else
    export APPHOST_CRYPTO_OPTS="$APPHOST_CRYPTO_OPTS -Denvironment=$silkcloudenv"
fi

./bin/apphost-crypto &
echo $! > ./logs/app.pid

