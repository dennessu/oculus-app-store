#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
pushd $DIR

YOUR_USER=shuz
cp /home/$YOUR_USER/main/apphost/apphost-identity/build/distributions/apphost-identity-0.0.1-SNAPSHOT.zip /home/silkcloud
cp /home/$YOUR_USER/main/apphost/apphost-crypto/build/distributions/apphost-crypto-0.0.1-SNAPSHOT.zip /home/silkcloud
cp /home/$YOUR_USER/main/apphost/apphost-dataloader/build/distributions/apphost-dataloader-0.0.1-SNAPSHOT.zip /home/silkcloud

scp /home/silkcloud/apphost-crypto-0.0.1-SNAPSHOT.zip 10.24.32.10:/var/silkcloud
scp /home/silkcloud/apphost-crypto-0.0.1-SNAPSHOT.zip 10.24.36.10:/var/silkcloud

./foreach-here.sh ppe-crypto-apps.txt << EOF
cd /var/silkcloud
unzip -o apphost-crypto-0.0.1-SNAPSHOT.zip
cd apphost-crypto-0.0.1-SNAPSHOT
./shutdown.sh
./startup.sh > /dev/null 2>&1
EOF

scp /home/silkcloud/apphost-identity-0.0.1-SNAPSHOT.zip 10.24.8.50:/var/silkcloud
scp /home/silkcloud/apphost-identity-0.0.1-SNAPSHOT.zip 10.24.12.50:/var/silkcloud

./foreach-here.sh ppe-apps.txt << EOF
cd /var/silkcloud
unzip -o apphost-identity-0.0.1-SNAPSHOT.zip
cd apphost-identity-0.0.1-SNAPSHOT
./shutdown.sh
./startup.sh > /dev/null 2>&1
EOF

scp /home/silkcloud/apphost-dataloader-0.0.1-SNAPSHOT.zip 10.24.32.10:/var/silkcloud
ssh 10.24.32.10 << EOF
cd /var/silkcloud
unzip -o apphost-dataloader-0.0.1-SNAPSHOT.zip
cd apphost-dataloader-0.0.1-SNAPSHOT
./dataloader.sh
EOF
