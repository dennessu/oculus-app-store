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
cp /home/$YOUR_USER/main/apphost/apphost-cli/build/distributions/$APP_NAME.zip /home/silkcloud

function pause() {
#read -p "Press any key to continue..."
echo pause
}

cd /home/silkcloud
unzip -o $APP_NAME.zip
ln -sf $APP_NAME apphost
cd apphost/dbsetup/pgha
./upload_script.sh ppe
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

./foreach-here.sh $ENV/masters.txt $ENV/crypto-dbs.txt << EOF
cd /var/silkcloud/pgha
./setup/setup_master.sh
nc -zv localhost 113 5432 6543
EOF

echo Running liquibase
ssh `cat $ENV/liquibase.txt` << EOF
cd apphost-identity-0.0.1-SNAPSHOT/dbsetup/liquibase
./createdb.sh -env:ppe -key:$CRYPTO_KEY
./updatedb.sh -env:ppe -key:$CRYPTO_KEY
EOF

./foreach-here.sh $ENV/masters.txt << EOF
cd /var/silkcloud/pgha
./londiste/londiste_root.sh
./util/base_backup.sh
EOF

./foreach-here.sh $ENV/secondaries.txt << EOF
cd /var/silkcloud/pgha
./setup/setup_slave.sh
nc -zv localhost 113 5432 6543
EOF

./foreach-here.sh $ENV/replicas.txt << EOF
cd /var/silkcloud/pgha
./setup/setup_replica.sh
nc -zv localhost 113 5432
EOF

echo Running test
ssh `head -n 1 $ENV/masters.txt` << EOF
cd /var/silkcloud/pgha
./test/test_master2slave.sh
EOF

popd
