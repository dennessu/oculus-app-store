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
: ${ENV_BASE?"Need to set ENV_BASE"}

# remove blue suffix
ENV=$(echo $ENV | sed s/-blue//)
echo Effective ENV: $ENV

echo Copying files...
LIQUIBASE_SETUP_SERVER=`head -n 1 $ENV/liquibase.txt`
cp $SOURCETREE_HOME/apphost/apphost-cli/build/distributions/$APP_NAME.zip /home/silkcloud

echo copying apphost to $LIQUIBASE_SETUP_SERVER
scp /home/silkcloud/$APP_NAME.zip $LIQUIBASE_SETUP_SERVER:/var/silkcloud

ssh $LIQUIBASE_SETUP_SERVER << EOF
set -e
cd /var/silkcloud
rm -rf $APP_NAME
unzip -o $APP_NAME.zip
ln -sfn $APP_NAME apphost
EOF

function pause() {
#read -p "Press any key to continue..."
echo pause
}

echo Uploading scripts
cd /home/silkcloud
rm -rf $APP_NAME
unzip -o $APP_NAME.zip
ln -sfn $APP_NAME apphost
cd apphost/dbsetup/pgha
./upload_script.sh $ENV
cd $DIR

./foreach-here.sh $ENV/masters.txt $ENV/secondaries.txt $ENV/bcps.txt $ENV/replicas.txt $ENV/crypto-dbs.txt << EOF
set -e
cd /var/silkcloud/pgha
./util/safe.sh
EOF

echo Running liquibase
ssh $LIQUIBASE_SETUP_SERVER << EOF
set -e
cd /var/silkcloud/apphost/dbsetup/liquibase
./createdb.sh -env:${ENV_BASE}.replica -key:$CRYPTO_KEY
./updatedb.sh -env:${ENV_BASE}.replica -key:$CRYPTO_KEY
./createdb.sh -env:$ENV -key:$CRYPTO_KEY
./updatedb.sh -env:$ENV -key:$CRYPTO_KEY
EOF

./foreach-here.sh $ENV/masters.txt << EOF
set -e
cd /var/silkcloud/pgha
./londiste/londiste_upgrade_root.sh
./util/safe.sh
EOF

./foreach-here.sh $ENV/secondaries.txt << EOF
set -e
cd /var/silkcloud/pgha
./util/safe.sh
EOF

./foreach-here.sh $ENV/replicas.txt << EOF
set -e
cd /var/silkcloud/pgha
./londiste/londiste_upgrade_leaf.sh
./util/safe.sh
EOF


popd
