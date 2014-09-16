#!/bin/bash

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

if [[ -z "$DATABASE_PASSWORD_HASH" ]]; then
  echo -n 'DATABASE_PASSWORD_HASH: '
  read -s DATABASE_PASSWORD_HASH
  export DATABASE_PASSWORD_HASH
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

if [[ -z "$SOURCETREE_HOME" ]]; then
  echo -n 'SOURCETREE_HOME: '
  read SOURCETREE_HOME
  export SOURCETREE_HOME
  echo
fi
