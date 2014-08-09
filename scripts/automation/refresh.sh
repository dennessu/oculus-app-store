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
if [[ -z "$ENV" ]]; then
  echo -n 'ENV: '
  read ENV
  export ENV
  echo
fi

if [[ -z "$ENV_BASE" ]]; then
  echo -n 'ENV_BASE: '
  read ENV_BASE
  export ENV_BASE
  echo
fi

if [[ -z "$ENV_PREFIX" ]]; then
  echo -n 'ENV_PREFIX: '
  read ENV_PREFIX
  export ENV_PREFIX
  echo
fi

./reset-config.sh
./refresh-couch.sh
./refresh-dbs.sh
./refresh-apps.sh
./dataloader.sh masterkey
./copy-masterkey.sh
./dataloader.sh
