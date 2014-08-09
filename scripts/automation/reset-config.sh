#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
pushd $DIR

set -e

if [[ -z "$CRYPTO_KEY" ]]; then
  echo -n 'CRYPTO_KEY: '
  read -s CRYPTO_KEY
  export CRYPTO_KEY
  echo
fi

if [[ -z "$OAUTH_CRYPTO_KEY" ]]; then
  echo -n 'OAUTH_CRYPTO_KEY: '
  read -s OAUTH_CRYPTO_KEY
  export OAUTH_CRYPTO_KEY
  echo
fi

export ENV=${ENV:-$1}
export ENV=${ENV:-ppe}

./foreach-here.sh $ENV/crypto-apps.txt << EOF
cat << EOFINNER > /etc/silkcloud/configuration.properties
profile=apphost-crypto
environment=ppe
crypto.core.key=$CRYPTO_KEY
oauth.crypto.key=$OAUTH_CRYPTO_KEY
common.conf.debugMode=false
EOFINNER
chmod 600 /etc/silkcloud/*
EOF

./foreach-here.sh $ENV/apps.txt << EOF
cat << EOFINNER > /etc/silkcloud/configuration.properties
profile=apphost-rest
environment=ppe
crypto.core.key=$CRYPTO_KEY
oauth.crypto.key=$OAUTH_CRYPTO_KEY
common.conf.debugMode=false
EOFINNER
chmod 600 /etc/silkcloud/*
EOF

./foreach-here.sh $ENV/utils.txt << EOF
cat << EOFINNER > /etc/silkcloud/configuration.properties
profile=apphost-jobs
environment=ppe
crypto.core.key=$CRYPTO_KEY
oauth.crypto.key=$OAUTH_CRYPTO_KEY
common.conf.debugMode=false
EOFINNER
chmod 600 /etc/silkcloud/*
EOF

./foreach-here.sh $ENV/crypto-dbs.txt $ENV/masters.txt $ENV/secondaries.txt $ENV/replicas.txt << EOF
cat << EOFINNER > /etc/silkcloud/configuration.properties
environment=ppe
crypto.core.key=$CRYPTO_KEY
oauth.crypto.key=$OAUTH_CRYPTO_KEY
common.conf.debugMode=false
EOFINNER
chmod 600 /etc/silkcloud/*
EOF
