#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
pushd $DIR

set -e

DOC_NAME=docs-bundle-0.0.1-SNAPSHOT
: ${ENV?"Need to set ENV"}
: ${ENV_BASE?"Need to set ENV_BASE"}
: ${SOURCETREE_HOME?"Need to set SOURCETREE_HOME"}

echo Copying files...
cp $SOURCETREE_HOME/apphost/docs-bundle/build/distributions/$DOC_NAME.zip /home/silkcloud
for var in $ENV/apps.txt; do
    for p in `cat $var`; do
        if [[ ! -z "$p" && ! $p == \#* ]]; then
            echo copying docs to $p
            scp -o "StrictHostKeyChecking no" /home/silkcloud/$DOC_NAME.zip $p:/var/silkcloud
        fi
    done
done

./foreach-here.sh $ENV/apps.txt << EOF
cd /var/silkcloud
(sudo initctl status silkcloud-docs | grep start) && sudo stop silkcloud-docs
rm -rf $DOC_NAME
unzip -o $DOC_NAME.zip
ln -sfn $DOC_NAME docs-bundle
sudo start silkcloud-docs
EOF
