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

APP_NAME=apphost-cli-0.0.1-SNAPSHOT
: ${SOURCETREE_HOME?"Need to set SOURCETREE_HOME"}
: ${ENV_BASE:?"Need to set ENV_BASE"}
: ${ENV_PREFIX?"Need to set ENV_PREFIX"}

echo Copying files...
COUCH_SETUP_SERVER=`head -n 1 $ENV/crypto-apps.txt`
cp $SOURCETREE_HOME/apphost/apphost-cli/build/distributions/$APP_NAME.zip /home/silkcloud

echo copying apphost to $COUCH_SETUP_SERVER
scp /home/silkcloud/$APP_NAME.zip $COUCH_SETUP_SERVER:/var/silkcloud

ssh $COUCH_SETUP_SERVER << EOF
cd /var/silkcloud
rm -rf $APP_NAME
unzip -o $APP_NAME.zip
ln -sfn $APP_NAME apphost

cd /var/silkcloud/apphost/dbsetup/couchdb
# python ./couchdbcmd.py dropdbs $ENV_BASE --prefix=$ENV_PREFIX --yes --key=$CRYPTO_KEY
python ./couchdbcmd.py createdbs $ENV_BASE --prefix=$ENV_PREFIX --yes --key=$CRYPTO_KEY
EOF
