#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
set -e 

pushd $DIR

echo -n 'CRYPTO_KEY: '
read -s CRYPTO_KEY
echo 

YOUR_USER=shuz
echo Copying files...
cp /home/$YOUR_USER/main/apphost/apphost-identity/build/distributions/apphost-identity-0.0.1-SNAPSHOT.zip /home/silkcloud
cp /home/$YOUR_USER/main/apphost/apphost-crypto/build/distributions/apphost-crypto-0.0.1-SNAPSHOT.zip /home/silkcloud
cp /home/$YOUR_USER/main/apphost/apphost-dataloader/build/distributions/apphost-dataloader-0.0.1-SNAPSHOT.zip /home/silkcloud

function pause() {
#read -p "Press any key to continue..."
echo pause
}

cd /home/silkcloud
unzip -o apphost-identity-0.0.1-SNAPSHOT.zip
cd apphost-identity-0.0.1-SNAPSHOT/dbsetup/pgha
./upload_script.sh ppe
cd $DIR

./foreach-here.sh ppe-masters.txt << EOF
/var/silkcloud/pgha/purge/purge_master.sh
EOF
pause

ssh 10.24.20.50 << EOF
/var/silkcloud/pgha/purge/purge_slave.sh
EOF
pause

ssh 10.24.22.50 << EOF
/var/silkcloud/pgha/purge/purge_replica.sh
EOF
pause

./foreach-here.sh ppe-masters.txt << EOF
cd /var/silkcloud/pgha
./setup/setup_master.sh
nc -zv localhost 113 5432 6543
EOF

ssh 10.24.34.10 << EOF
cd apphost-identity-0.0.1-SNAPSHOT/dbsetup/liquibase
./createdb.sh -env:ppe -key:$CRYPTO_KEY
./updatedb.sh -env:ppe -key:$CRYPTO_KEY
EOF

ssh 10.24.16.50 << EOF
cd /var/silkcloud/pgha
./londiste/londiste_root.sh
./util/base_backup.sh
EOF

ssh 10.24.20.50 << EOF
cd /var/silkcloud/pgha
./setup/setup_slave.sh
nc -zv localhost 113 5432 6543
EOF

ssh 10.24.22.50 << EOF
cd /var/silkcloud/pgha
./setup/setup_replica.sh
nc -zv localhost 113 5432
EOF

ssh 10.24.16.50 << EOF
cd /var/silkcloud/pgha
./test/test_master2slave.sh
EOF

popd

