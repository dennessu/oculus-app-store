#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
set -e

pushd $DIR

if [[ -z "$CRYPTO_KEY" ]]; then
  echo -n 'CRYPTO_KEY: '
  read -s CRYPTO_KEY
  export CRYPTO_KEY
  echo
fi

export ENV=${ENV:-$1}
export ENV=${ENV:-ppe}

ssh `head -n 1 $ENV/crypto-apps.txt` << EOF
cd /var/silkcloud/apphost/dbsetup/couchdb
python ./couchdbcmd.py dropdbs ppe --prefix=ppe_ --yes
python ./couchdbcmd.py createdbs ppe --prefix=ppe_ --yes
EOF
