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
: ${ENV?"Need to set ENV"}

echo Copying files...
LIQUIBASE_SETUP_SERVER=`head -n 1 $ENV/liquibase.txt`
cp $SOURCETREE_HOME/apphost/apphost-cli/build/distributions/$APP_NAME.zip /home/silkcloud

echo copying apphost to $LIQUIBASE_SETUP_SERVER
scp /home/silkcloud/$APP_NAME.zip $LIQUIBASE_SETUP_SERVER:/var/silkcloud

ssh $LIQUIBASE_SETUP_SERVER << EOF
cd /var/silkcloud
rm -rf $APP_NAME
unzip -o $APP_NAME.zip
ln -sfn $APP_NAME apphost
EOF

function pause() {
#read -p "Press any key to continue..."
echo pause
}

cd /home/silkcloud
rm -rf $APP_NAME
unzip -o $APP_NAME.zip
ln -sfn $APP_NAME apphost
cd apphost/dbsetup/pgha
./upload_script.sh $ENV
cd $DIR

./foreach-here.sh $ENV/masters.txt $ENV/crypto-dbs.txt << EOF
/var/silkcloud/pgha/purge/purge_master.sh
EOF
pause

./foreach-here.sh $ENV/secondaries.txt << EOF
/var/silkcloud/pgha/purge/purge_slave.sh
EOF
pause

./foreach-here.sh $ENV/replicas.txt << EOF
/var/silkcloud/pgha/purge/purge_replica.sh
EOF
pause

popd
