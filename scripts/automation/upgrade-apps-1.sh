#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
pushd $DIR

set -e

APP_NAME=apphost-cli-0.0.1-SNAPSHOT
: ${ENV?"Need to set ENV"}
: ${ENV_BASE?"Need to set ENV_BASE"}
: ${SOURCETREE_HOME?"Need to set SOURCETREE_HOME"}

if [[ ! -d ./newrelic ]]; then
    echo "newrelic is not found."
    exit 1
fi

echo Copying files...
cp $SOURCETREE_HOME/apphost/apphost-cli/build/distributions/$APP_NAME.zip /home/silkcloud
for var in $ENV/apps-1.txt $ENV/crypto-apps-1.txt do
    for p in `cat $var`; do
        if [[ ! -z "$p" && ! $p == \#* ]]; then
            echo copying apphost to $p
            scp -o "StrictHostKeyChecking no" /home/silkcloud/$APP_NAME.zip $p:/var/silkcloud
        fi
    done
done

./foreach-here.sh $ENV/crypto-apps-1.txt $ENV/apps-1.txt << EOF
cd /var/silkcloud
(sudo initctl status silkcloud-apphost | grep start) && sudo stop silkcloud-apphost
rm -rf $APP_NAME
unzip -o $APP_NAME.zip
ln -sfn $APP_NAME apphost
EOF

echo Copying newrelic files...
cp $SOURCETREE_HOME/apphost/apphost-cli/build/distributions/$APP_NAME.zip /home/silkcloud
for var in $ENV/crypto-apps-1.txt $ENV/apps-1.txt do
    for p in `cat $var`; do
        if [[ ! -z "$p" && ! $p == \#* ]]; then
            echo copying newrelic to $p
            scp -o "StrictHostKeyChecking no" -r ./newrelic/. $p:/var/silkcloud/apphost/newrelic/
        fi
    done
done

./foreach-here.sh $ENV/crypto-apps-1.txt << EOF
sed -e 's@^export APPHOST_OPTS=".*"\$@export APPHOST_OPTS="-javaagent:/var/silkcloud/apphost/newrelic/newrelic.jar -Dnewrelic.environment=cry$ENV_BASE"@g' -i /var/silkcloud/apphost/startup.sh
echo Updated to \`grep '^export APPHOST_OPTS=' /var/silkcloud/apphost/startup.sh\`

sudo start silkcloud-apphost
EOF

./foreach-here.sh $ENV/apps-1.txt << EOF
sed -e 's@^export APPHOST_OPTS=".*"\$@export APPHOST_OPTS="-javaagent:/var/silkcloud/apphost/newrelic/newrelic.jar -Dnewrelic.environment=$ENV_BASE"@g' -i /var/silkcloud/apphost/startup.sh
echo Updated to \`grep '^export APPHOST_OPTS=' /var/silkcloud/apphost/startup.sh\`

sudo start silkcloud-apphost
EOF

