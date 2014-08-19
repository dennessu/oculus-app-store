#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
set -e

pushd $DIR

if [[ -z "$DATABASE_PASSWORD_HASH" ]]; then
  echo -n 'DATABASE_PASSWORD_HASH: '
  read -s DATABASE_PASSWORD_HASH
  export DATABASE_PASSWORD_HASH
  echo
fi
: ${ENV?"Need to set ENV"}

./foreach-here.sh $ENV/masters.txt $ENV/secondaries.txt $ENV/replicas.txt $ENV/crypto-dbs.txt << EOF
set -e
echo '"silkcloud" "md5$DATABASE_PASSWORD_HASH"' > ~/.pgbouncer_auth
chmod 600 ~/.pgbouncer_auth
EOF

popd
