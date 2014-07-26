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

./reset-config.sh
./refresh-couch.sh
./refresh-dbs.sh
./refresh-apps.sh
./dataloader.sh masterkey
./copy-masterkey.sh
./dataloader.sh
