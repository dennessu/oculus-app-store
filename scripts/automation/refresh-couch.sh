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

YOUR_USER=shuz
APP_NAME=apphost-cli-0.0.1-SNAPSHOT
export ENV=${ENV:-$1}
export ENV=${ENV:-ppe}

echo Copying files...
COUCH_SETUP_SERVER=`head -n 1 $ENV/crypto-apps.txt`
cp /home/$YOUR_USER/main/apphost/apphost-cli/build/distributions/$APP_NAME.zip /home/silkcloud

echo copying apphost to $COUCH_SETUP_SERVER
scp /home/silkcloud/$APP_NAME.zip $COUCH_SETUP_SERVER:/var/silkcloud

ssh $COUCH_SETUP_SERVER << EOF
cd /var/silkcloud
unzip -o $APP_NAME.zip
ln -sf $APP_NAME apphost

cd /var/silkcloud/apphost/dbsetup/couchdb
python ./couchdbcmd.py dropdbs ppe --prefix=ppe_ --yes
python ./couchdbcmd.py createdbs ppe --prefix=ppe_ --yes
EOF
