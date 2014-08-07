#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
pushd $DIR

set -e

YOUR_USER=shuz
APP_NAME=apphost-cli-0.0.1-SNAPSHOT
export ENV=${ENV:-$1}
export ENV=${ENV:-ppe}

echo Copying files...
cp /home/$YOUR_USER/main/apphost/apphost-cli/build/distributions/$APP_NAME.zip /home/silkcloud
for var in $ENV/apps.txt $ENV/crypto-apps.txt; do
    for p in `cat $var`; do
        if [[ ! -z "$p" && ! $p == \#* ]]; then
            echo copying apphost to $p
            scp /home/silkcloud/$APP_NAME.zip $p:/var/silkcloud
        fi
    done
done

./foreach-here.sh $ENV/crypto-apps.txt $ENV/apps.txt $ENV/utils.txt << EOF
cd /var/silkcloud
(sudo initctl status silkcloud-apphost | grep start) && sudo stop silkcloud-apphost
rm -rf $APP_NAME
unzip -o $APP_NAME.zip
ln -sfn $APP_NAME apphost
sudo start silkcloud-apphost
EOF
